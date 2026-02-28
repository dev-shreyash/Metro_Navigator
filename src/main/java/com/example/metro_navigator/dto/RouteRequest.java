package com.example.metro_navigator.dto;

import lombok.Data;

@Data
public class RouteRequest {
    private String source;
    private String destination;
}