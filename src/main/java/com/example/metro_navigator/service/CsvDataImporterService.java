package com.example.metro_navigator.service;

import com.example.metro_navigator.entity.MetroEdge;
import com.example.metro_navigator.entity.Station;
import com.example.metro_navigator.repository.MetroEdgeRepository;
import com.example.metro_navigator.repository.StationRepository;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.io.Reader;

@Service
public class CsvDataImporterService {

    private final StationRepository stationRepository;
    private final MetroEdgeRepository metroEdgeRepository;

    public CsvDataImporterService(StationRepository stationRepository, MetroEdgeRepository metroEdgeRepository) {
        this.stationRepository = stationRepository;
        this.metroEdgeRepository = metroEdgeRepository;
    }

    @PostConstruct
    @Transactional
    public void importCsvData() {
        if (stationRepository.count() > 0) {
            System.out.println("Data already loaded. Skipping import.");
            return;
        }

        try {
            loadStations();
            loadEdges();
            System.out.println("Metro data imported successfully!");
        } catch (Exception e) {
            throw new RuntimeException("Failed to import CSV data: " + e.getMessage(), e);
        }
    }

    private void loadStations() throws Exception {
        Reader reader = new InputStreamReader(new ClassPathResource("station.csv").getInputStream());
        
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

        Iterable<CSVRecord> records = format.parse(reader);

        for (CSVRecord record : records) {
            Station station = new Station();
            station.setId(Long.parseLong(record.get("id")));
            station.setLine(record.get("line"));
            station.setName(record.get("name"));
            
            stationRepository.save(station);
        }
    }

    private void loadEdges() throws Exception {
        Reader reader = new InputStreamReader(new ClassPathResource("metro_edges.csv").getInputStream());
        
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setCommentMarker('#')
                .build();

        Iterable<CSVRecord> records = format.parse(reader);

        for (CSVRecord record : records) {
            MetroEdge edge = new MetroEdge();
            
            edge.setDistance(Double.parseDouble(record.get("distance_km")));
            edge.setTime(Double.parseDouble(record.get("time_min")));
            
            Long sourceId = Long.parseLong(record.get("source_id"));
            Long destId = Long.parseLong(record.get("destination_id"));
            
            Station source = stationRepository.findById(sourceId)
                    .orElseThrow(() -> new RuntimeException("Source station not found: " + sourceId));
            Station destination = stationRepository.findById(destId)
                    .orElseThrow(() -> new RuntimeException("Destination station not found: " + destId));
            
            edge.setSource(source);
            edge.setDestination(destination);
            
            metroEdgeRepository.save(edge);
        }
    }
}