package com.example.metro_navigator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.metro_navigator.entity.MetroEdge;

@Repository
public interface MetroEdgeRepository extends JpaRepository<MetroEdge, Long> {

    // Fetch the source and destination stations for each edge
  @Query("SELECT e FROM MetroEdge e JOIN FETCH e.source JOIN FETCH e.destination")
    List<MetroEdge> findAllWithStations();
}