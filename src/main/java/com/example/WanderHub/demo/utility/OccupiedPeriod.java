package com.example.WanderHub.demo.utility;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public class OccupiedPeriod {
    private LocalDate start; // Data di inizio occupazione
    private LocalDate end;   // Data di fine occupazione

    // Costruttori
    public OccupiedPeriod() {}

    public OccupiedPeriod(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    // Getter e Setter
    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "OccupiedPeriod{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}
