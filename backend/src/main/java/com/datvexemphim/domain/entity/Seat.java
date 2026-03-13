package com.datvexemphim.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_seat_room_code", columnNames = {"room_id", "seatCode"})
        },
        indexes = {
                @Index(name = "idx_seats_room", columnList = "room_id")
        })
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false, length = 5)
    private String rowLabel;

    @Column(nullable = false)
    private Integer colNumber;

    @Column(nullable = false, length = 10)
    private String seatCode;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public String getRowLabel() { return rowLabel; }
    public void setRowLabel(String rowLabel) { this.rowLabel = rowLabel; }
    public Integer getColNumber() { return colNumber; }
    public void setColNumber(Integer colNumber) { this.colNumber = colNumber; }
    public String getSeatCode() { return seatCode; }
    public void setSeatCode(String seatCode) { this.seatCode = seatCode; }
}

