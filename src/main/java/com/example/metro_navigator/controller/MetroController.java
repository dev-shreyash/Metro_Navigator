package com.example.metro_navigator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.metro_navigator.dto.RouteRequest;
import com.example.metro_navigator.dto.RouteResponse;
import com.example.metro_navigator.service.MetroNavigationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/metro")
@RequiredArgsConstructor
public class MetroController {

    private final MetroNavigationService navigationService;

    // The endpoint to find the shortest path
    // Example URL: http://localhost:8080/api/v1/metro/route?source=Versova&destination=Ghatkopar
    @PostMapping("/route")
    public ResponseEntity<RouteResponse> getRoute(@RequestBody RouteRequest request) {
            
        try {
            RouteResponse response = navigationService.calculateFastestRoute(request.getSource(), request.getDestination());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(RouteResponse.builder()
                    .source(request.getSource())
                    .destination(request.getDestination())
                    .build());
        }
    }
}