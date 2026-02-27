package com.example.metro_navigator.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.springframework.stereotype.Service;

import com.example.metro_navigator.dto.RouteResponse;
import com.example.metro_navigator.entity.MetroEdge;
import com.example.metro_navigator.entity.Station;
import com.example.metro_navigator.repository.MetroEdgeRepository;
import com.example.metro_navigator.repository.StationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetroNavigationService {

    private final StationRepository stationRepo;
    private final MetroEdgeRepository edgeRepo;

    // Helper class for the PriorityQueue
    private static class NodeRecord implements Comparable<NodeRecord> {
        Station station;
        double distance;

        NodeRecord(Station station, double distance) {
            this.station = station;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeRecord other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    public RouteResponse calculateFastestRoute(String sourceName, String destName) {
        Station start = stationRepo.findByName(sourceName)
                .orElseThrow(() -> new RuntimeException("Source station not found: " + sourceName));
        Station end = stationRepo.findByName(destName)
                .orElseThrow(() -> new RuntimeException("Destination station not found: " + destName));

        // 1. Build Adjacency List from Database
        List<MetroEdge> allEdges = edgeRepo.findAll();
        Map<Station, List<MetroEdge>> graph = new HashMap<>();
        
        for (MetroEdge edge : allEdges) {
            graph.computeIfAbsent(edge.getSource(), k -> new ArrayList<>()).add(edge);
            MetroEdge reverseEdge = new MetroEdge(null, edge.getDestination(), edge.getSource(), edge.getDistance(), edge.getTime());
            graph.computeIfAbsent(edge.getDestination(), k -> new ArrayList<>()).add(reverseEdge);
        }

        // 2. Dijkstra's Initialization
        PriorityQueue<NodeRecord> pq = new PriorityQueue<>();
        Map<Station, Double> minDistance = new HashMap<>();
        Map<Station, Station> parentMap = new HashMap<>();
        Map<Station, Double> travelTime = new HashMap<>();

        for (Station s : stationRepo.findAll()) {
            minDistance.put(s, Double.MAX_VALUE);
        }

        minDistance.put(start, 0.0);
        travelTime.put(start, 0.0);
        pq.add(new NodeRecord(start, 0.0));

        // 3. Process the Graph
        while (!pq.isEmpty()) {
            NodeRecord currentRecord = pq.poll();
            Station current = currentRecord.station;

            if (current.equals(end)) break; // Found the destination

            if (currentRecord.distance > minDistance.get(current)) continue;

            List<MetroEdge> neighbors = graph.getOrDefault(current, new ArrayList<>());
            for (MetroEdge edge : neighbors) {
                Station neighbor = edge.getDestination();
                double newDist = minDistance.get(current) + edge.getDistance();

                if (newDist < minDistance.get(neighbor)) {
                    minDistance.put(neighbor, newDist);
                    parentMap.put(neighbor, current);
                    travelTime.put(neighbor, travelTime.get(current) + edge.getTime());
                    pq.add(new NodeRecord(neighbor, newDist));
                }
            }
        }

        // 4. Reconstruct the Path
        return buildResponse(start, end, parentMap, minDistance.get(end), travelTime.get(end));
    }

    private RouteResponse buildResponse(Station start, Station end, Map<Station, Station> parentMap, double totalDist, double totalTime) {
        LinkedList<String> path = new LinkedList<>();
        Station curr = end;

        while (curr != null) {
            path.addFirst(curr.getName());
            curr = parentMap.get(curr);
        }

        if (path.size() == 1 && !start.equals(end)) {
            throw new RuntimeException("No route exists between these stations.");
        }

        int stops = path.size() - 1;
        int fare = (int) Math.ceil(10 + (2 * totalDist)); // Base Rs 10 + Rs 2 per km

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
}