package com.example.metro_navigator.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteResponse {
    @Builder.Default
    private String project = "Metro Navigator";

    private String source;
    private String destination;
    private int stops;
    private double totalDistanceKm;
    private double estimatedTimeMins;
    private int fareRs;
    private List<String> routePath;
}