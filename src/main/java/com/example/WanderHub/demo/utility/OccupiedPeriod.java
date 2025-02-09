package com.example.WanderHub.demo.utility;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public class OccupiedPeriod {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate end;

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
