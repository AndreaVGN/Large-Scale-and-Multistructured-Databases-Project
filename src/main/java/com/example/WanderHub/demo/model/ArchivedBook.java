package com.example.WanderHub.demo.model;

import com.example.WanderHub.demo.utility.OccupiedPeriod;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "archivedBooks")
/*
public class ArchivedBook {

    private String accommodationId;
    private String hostUsername;
    private String city;
    private List<OccupiedPeriod> occupiedDates; // Elenco dei periodi occupati
    private String username;
    private String email;
    private String birthPlace;
    private String address;
    private int addressNumber;
    private String birthDate;
    private int costPerNight;
    private String[] guestFirstNames; // Array dei nomi degli ospiti
    private String[] guestLastNames;

    // Costruttore
    public ArchivedBook(String accommodationId,String hostUsername, String city, List<OccupiedPeriod> occupiedDates, String username,
                           String email, String birthPlace, String address, int addressNumber, String birthDate,
                           String[] guestFirstNames, String[] guestLastNames, int costPerNight) {
        this.accommodationId = accommodationId;
        this.hostUsername = hostUsername;
        this.city = city;
        this.occupiedDates = occupiedDates;
        this.username = username;
        this.email = email;
        this.birthPlace = birthPlace;
        this.address = address;
        this.addressNumber = addressNumber;
        this.birthDate = birthDate;
        this.guestFirstNames = guestFirstNames;
        this.guestLastNames = guestLastNames;
        this.costPerNight = costPerNight;
    }

    // Getter e Setter
    public String getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(String accommodationId) {
        this.accommodationId = accommodationId;
    }
    public String getHostUsername() {
        return hostUsername;
    }
    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public List<OccupiedPeriod> getOccupiedDates() {
        return occupiedDates;
    }

    public void setOccupiedDates(List<OccupiedPeriod> occupiedDates) {
        this.occupiedDates = occupiedDates;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(int addressNumber) {
        this.addressNumber = addressNumber;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String[] getGuestFirstNames() {
        return guestFirstNames;
    }

    public void setGuestFirstNames(String[] guestFirstNames) {
        this.guestFirstNames = guestFirstNames;
    }

    public String[] getGuestLastNames() {
        return guestLastNames;
    }

    public void setGuestLastNames(String[] guestLastNames) {
        this.guestLastNames = guestLastNames;
    }

    public LocalDate getStartDate() {
        if (occupiedDates != null && !occupiedDates.isEmpty()) {
            return occupiedDates.get(0).getStart(); // Restituisce la data di inizio del primo periodo
        }
        return null; // Se non ci sono periodi, restituisci null
    }
    public LocalDate getEndDate() {
        if (occupiedDates != null && !occupiedDates.isEmpty()) {
            return occupiedDates.get(0).getEnd(); // Restituisce la data di inizio del primo periodo
        }
        return null; // Se non ci sono periodi, restituisci null
    }
    public void setStartDate(LocalDate startDate) {
        if (occupiedDates != null && !occupiedDates.isEmpty()) {
            occupiedDates.get(0).setStart(startDate); // Imposta la data di inizio del primo periodo
        }
    }

    public void setEndDate(LocalDate endDate) {
        if (occupiedDates != null && !occupiedDates.isEmpty()) {
            occupiedDates.get(0).setEnd(endDate); // Imposta la data di fine del primo periodo
        }
    }

    public void setCostPerNight(int costPerNight) {
        this.costPerNight = costPerNight;
    }

    public int getCostPerNight() {
        return costPerNight;
    }

    // Metodo toString
    @Override
    public String toString() {
        return "ArchivedBooking{" +
                "accommodationId='" + accommodationId + '\'' +
                ", hostUsername='" + hostUsername + '\'' +
                ", city='" + city + '\'' +
                ", occupiedDates=" + occupiedDates +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", address='" + address + '\'' +
                ", addressNumber=" + addressNumber +
                ", birthDate='" + birthDate + '\'' +
                ", costPerNight=" + costPerNight +
                ", guestFirstNames=" + (guestFirstNames != null ? String.join(", ", guestFirstNames) : "null") +
                ", guestLastNames=" + (guestLastNames != null ? String.join(", ", guestLastNames) : "null") +
                '}';
    }
}*/

public class ArchivedBook extends Book {

    private String accommodationId;  // ID della casa associata alla prenotazione
    private String city;             // Città in cui si trova l'accommodation
    private String hostUsername;     // Username dell'host che offre l'accommodation
    private double costPerNight;     // Costo per notte dell'accommodation

    public ArchivedBook() {
        super();
    }

    // Costruttore con parametri per inizializzare tutti i campi
    public ArchivedBook(List<OccupiedPeriod> occupiedDates, String username, String email,
                        String birthPlace, String address, int addressNumber, String birthDate,
                        String[] guestFirstNames, String[] guestLastNames, String accommodationId,
                        String city, String hostUsername, double costPerNight) {
        super(occupiedDates, username, email, birthPlace, address, addressNumber, birthDate,
                guestFirstNames, guestLastNames);
        this.accommodationId = accommodationId;
        this.city = city;
        this.hostUsername = hostUsername;
        this.costPerNight = costPerNight;
    }

    // Getter e Setter per i nuovi campi
    public String getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(String accommodationId) {
        this.accommodationId = accommodationId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }

    public double getCostPerNight() {
        return costPerNight;
    }

    public void setCostPerNight(double costPerNight) {
        this.costPerNight = costPerNight;
    }

    @Override
    public String toString() {
        return "ArchivedBook{" +
                "accommodationId='" + accommodationId + '\'' +
                ", city='" + city + '\'' +
                ", hostUsername='" + hostUsername + '\'' +
                ", costPerNight=" + costPerNight +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", birthPlace='" + getBirthPlace() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", addressNumber=" + getAddressNumber() +
                ", birthDate='" + getBirthDate() + '\'' +
                '}';
    }
}


