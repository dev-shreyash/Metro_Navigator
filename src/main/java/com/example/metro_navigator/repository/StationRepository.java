package com.example.metro_navigator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.metro_navigator.entity.Station;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    
    Optional<Station> findByName(String name);
}