package com.example.metro_navigator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "metro_edges", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"source_id", "destination_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetroEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_id", nullable = false)
    private Station source;

    @ManyToOne
    @JoinColumn(name = "destination_id", nullable = false)
    private Station destination;

    @Column(nullable = false)
    private double distance;

    @Column(nullable = false)
    private double time;
}