package com.example.WanderHub.demo.DTO;
import java.time.LocalDate;
public class PendingBooksDTO {

        private String username;
        private String accommodationId;
        private LocalDate startDate;
        private LocalDate endDate;

        public PendingBooksDTO(String username, String accommodationId, LocalDate startDate, LocalDate endDate) {
            this.username = username;
            this.accommodationId = accommodationId;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAccommodationId() {
            return accommodationId;
        }

        public void setAccommodationId(String accommodationId) {
            this.accommodationId = accommodationId;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }
        public LocalDate getEndDate() {
            return endDate;
        }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        @Override
        public String toString() {
            return "BookingDTO{" +
                    "username='" + username + '\'' +
                    ", accommodationId=" + accommodationId +
                    ", startDate=" + startDate +
                    ", endDate=" + endDate +
                    '}';
        }
    }


