package com.example.metro_navigator.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.metro_navigator.entity.MetroEdge;
import com.example.metro_navigator.entity.Station;
import com.example.metro_navigator.repository.MetroEdgeRepository;
import com.example.metro_navigator.repository.StationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final StationRepository stationRepo;
    private final MetroEdgeRepository edgeRepo;

    @Override
    public void run(String... args) throws Exception {
        if (stationRepo.count() == 0) {
            System.out.println("Seeding Mumbai Metro Data...");

            Station versova = stationRepo.save(new Station(null, "Versova", "Blue Line"));
            Station dnNagar = stationRepo.save(new Station(null, "DN Nagar", "Blue Line"));
            Station andheri = stationRepo.save(new Station(null, "Andheri", "Blue Line"));
            Station weh = stationRepo.save(new Station(null, "WEH", "Blue Line"));
            Station ghatkopar = stationRepo.save(new Station(null, "Ghatkopar", "Blue Line"));
            
            Station dadar = stationRepo.save(new Station(null, "Dadar", "Aqua Line"));
            Station bkc = stationRepo.save(new Station(null, "BKC", "Aqua Line"));

            edgeRepo.save(new MetroEdge(null, versova, dnNagar, 1.0, 3.0));
            edgeRepo.save(new MetroEdge(null, dnNagar, andheri, 2.0, 6.0));
            edgeRepo.save(new MetroEdge(null, andheri, weh, 1.0, 3.0));
            edgeRepo.save(new MetroEdge(null, weh, ghatkopar, 6.0, 15.0));
            
            edgeRepo.save(new MetroEdge(null, andheri, bkc, 5.0, 12.0));
            edgeRepo.save(new MetroEdge(null, bkc, dadar, 4.2, 9.0));

            System.out.println("Data Seeding Complete!");
        }
    }
}