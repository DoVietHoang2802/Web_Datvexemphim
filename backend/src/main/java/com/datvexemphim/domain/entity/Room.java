package com.datvexemphim.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms", indexes = {
        @Index(name = "idx_rooms_name", columnList = "name", unique = true)
})
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer totalRows;

    @Column(nullable = false)
    private Integer totalCols;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getTotalRows() { return totalRows; }
    public void setTotalRows(Integer totalRows) { this.totalRows = totalRows; }
    public Integer getTotalCols() { return totalCols; }
    public void setTotalCols(Integer totalCols) { this.totalCols = totalCols; }
}

