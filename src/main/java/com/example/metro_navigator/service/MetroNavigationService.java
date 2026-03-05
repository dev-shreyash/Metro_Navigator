package com.example.metro_navigator.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.metro_navigator.dto.RouteResponse;
import com.example.metro_navigator.entity.MetroEdge;
import com.example.metro_navigator.entity.Station;
import com.example.metro_navigator.repository.MetroEdgeRepository;
import com.example.metro_navigator.repository.StationRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetroNavigationService {

    private final StationRepository stationRepo;
    private final MetroEdgeRepository edgeRepo;

    // --- IN-MEMORY CACHE ---
    private final Map<Long, List<MetroEdge>> adjacencyList = new HashMap<>();
    private final Map<String, List<Station>> stationsByName = new HashMap<>();
    private final Map<Long, Station> stationById = new HashMap<>();
    private List<Station> allStations = new ArrayList<>();

    // 1. The Engine Start: Build the graph ONCE when Spring Boot boots up
    @PostConstruct
    public void initGraph() {
        System.out.println("Building in-memory Metro Graph...");
        adjacencyList.clear();
        stationsByName.clear();
        stationById.clear();

        allStations = stationRepo.findAll();
        for (Station s : allStations) {
            // This handles Interchanges! The same name will naturally map to TWO stations in this list.
            stationsByName.computeIfAbsent(s.getName(), k -> new ArrayList<>()).add(s);
            stationById.put(s.getId(), s);
            adjacencyList.put(s.getId(), new ArrayList<>());
        }

        List<MetroEdge> allEdges = edgeRepo.findAllWithStations();
        for (MetroEdge edge : allEdges) {
            adjacencyList.get(edge.getSource().getId()).add(edge);

            // Add reverse edge for undirected graph
            MetroEdge reverseEdge = new MetroEdge(null, edge.getDestination(), edge.getSource(), edge.getDistance(), edge.getTime());
            adjacencyList.get(edge.getDestination().getId()).add(reverseEdge);
        }
        System.out.println("Metro Graph loaded into RAM successfully!");
    }

    private static class NodeRecord implements Comparable<NodeRecord> {

        Long stationId;
        double distance;

        NodeRecord(Long stationId, double distance) {
            this.stationId = stationId;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeRecord other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    // 2. The Core Logic: Zero Database Calls inside this method
    @Cacheable(value = "routes", key = "#sourceName.concat('-').concat(#destName)")
    public RouteResponse calculateFastestRoute(String sourceName, String destName) {
        List<Station> startNodes = stationsByName.get(sourceName);
        List<Station> endNodes = stationsByName.get(destName);

        if (startNodes == null || startNodes.isEmpty()) {
            throw new RuntimeException("Source station not found: " + sourceName);
        }
        if (endNodes == null || endNodes.isEmpty()) {
            throw new RuntimeException("Destination station not found: " + destName);
        }

        PriorityQueue<NodeRecord> pq = new PriorityQueue<>();
        Map<Long, Double> minDistance = new HashMap<>();
        Map<Long, Long> parentMap = new HashMap<>();
        Map<Long, Double> travelTime = new HashMap<>();

        for (Station s : allStations) {
            minDistance.put(s.getId(), Double.MAX_VALUE);
        }

        // Initialize ALL valid start nodes (Crucial for Interchanges)
        for (Station startNode : startNodes) {
            minDistance.put(startNode.getId(), 0.0);
            travelTime.put(startNode.getId(), 0.0);
            pq.add(new NodeRecord(startNode.getId(), 0.0));
        }

        Long finalDestinationId = null;

        while (!pq.isEmpty()) {
            NodeRecord currentRecord = pq.poll();
            Long currentId = currentRecord.stationId;

            // Check if the current node is ONE OF the valid destination nodes
            if (endNodes.stream().anyMatch(endNode -> endNode.getId().equals(currentId))) {
                finalDestinationId = currentId;
                break;
            }

            if (currentRecord.distance > minDistance.get(currentId)) {
                continue;
            }

            for (MetroEdge edge : adjacencyList.get(currentId)) {
                Long neighborId = edge.getDestination().getId();
                double newDist = minDistance.get(currentId) + edge.getDistance();

                if (newDist < minDistance.get(neighborId)) {
                    minDistance.put(neighborId, newDist);
                    parentMap.put(neighborId, currentId);
                    travelTime.put(neighborId, travelTime.getOrDefault(currentId, 0.0) + edge.getTime());
                    pq.add(new NodeRecord(neighborId, newDist));
                }
            }
        }

        if (finalDestinationId == null) {
            throw new RuntimeException("No route exists between these stations.");
        }

        return buildResponse(startNodes.get(0), stationById.get(finalDestinationId), parentMap, minDistance.get(finalDestinationId), travelTime.get(finalDestinationId));
    }

    private RouteResponse buildResponse(Station start, Station end, Map<Long, Long> parentMap, double totalDist, double totalTime) {
        LinkedList<String> path = new LinkedList<>();
        Long currId = end.getId();

        while (currId != null) {
            path.addFirst(stationById.get(currId).getName());
            currId = parentMap.get(currId);
        }

        int stops = path.size() - 1;
        int fare = (int) Math.ceil(10 + (2 * totalDist));

        return RouteResponse.builder()
                .source(start.getName())
                .destination(end.getName())
                .stops(stops)
                .totalDistanceKm(Math.round(totalDist * 100.0) / 100.0)
                .estimatedTimeMins(Math.round(totalTime))
                .fareRs(fare)
                .routePath(path)
                .build();
    }

    @CacheEvict(value = "routes", allEntries = true)
    public void updateEdgeData(MetroEdge updatedEdge) {
        edgeRepo.save(updatedEdge);
        // We MUST rebuild the in-memory graph if the DB changes!
        initGraph();
        System.out.println("Database updated. Graph rebuilt and cache evicted.");
    }
}
