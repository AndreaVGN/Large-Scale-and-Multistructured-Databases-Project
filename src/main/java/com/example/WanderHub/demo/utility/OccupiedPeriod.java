package com.example.WanderHub.demo.utility;

public class OccupiedPeriod {
    private String startDate; // Data di inizio occupazione
    private String endDate;   // Data di fine occupazione

    // Costruttori
    public OccupiedPeriod() {}

    public OccupiedPeriod(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getter e Setter
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "OccupiedPeriod{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
