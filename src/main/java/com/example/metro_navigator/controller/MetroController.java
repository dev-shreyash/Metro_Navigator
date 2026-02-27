package com.example.metro_navigator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/route")
    public ResponseEntity<RouteResponse> getRoute(
            @RequestParam String source,
            @RequestParam String destination) {
            
        try {
            RouteResponse response = navigationService.calculateFastestRoute(source, destination);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(RouteResponse.builder()
                    .source(source)
                    .destination(destination)
                    .build());
        }
    }
}