package com.example.metro_navigator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.metro_navigator.entity.MetroEdge;

@Repository
public interface MetroEdgeRepository extends JpaRepository<MetroEdge, Long> {
    
  
}