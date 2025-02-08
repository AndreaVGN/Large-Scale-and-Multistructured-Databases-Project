package com.example.WanderHub.demo.utility;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.RegisteredUser;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.Set;

public class Validator {

    // Metodo per validare un oggetto di tipo Accommodation
    public static void validateAccommodation(Accommodation accommodation) {
        // Check if description is not empty
        if (accommodation.getDescription() == null || accommodation.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }

        // Check if type is not empty
        if (accommodation.getType() == null || accommodation.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Accommodation type cannot be empty.");
        }

        // Check if facilities are not empty
        if (accommodation.getFacilities() == null) {
            throw new IllegalArgumentException("At least one facility must be selected.");
        }

        // Check if place, city, and address are not empty
        if (accommodation.getPlace() == null || accommodation.getPlace().trim().isEmpty() ||
                accommodation.getCity() == null || accommodation.getCity().trim().isEmpty() ||
                accommodation.getAddress() == null || accommodation.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Place, city, and address cannot be empty.");
        }

        // Check if host username is not empty
        if (accommodation.getHostUsername() == null || accommodation.getHostUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Host username cannot be empty.");
        }

        // Check if latitude and longitude are not empty
        if (accommodation.getLatitude() < -90 || accommodation.getLatitude() > 90 ||
                accommodation.getLongitude() < -180 || accommodation.getLongitude() > 180
        ) {
            throw new IllegalArgumentException("Latitude and longitude out of range");
        }

        // Check if occupied dates are not empty
        if (!accommodation.getOccupiedDates().isEmpty()) {
            throw new IllegalArgumentException("Occupied date must be empty.");
        }

        // Check if max guest size is not empty or zero
        if (accommodation.getMaxGuestSize() <= 0) {
            throw new IllegalArgumentException("Max guest size must be greater than zero.");
        }

        // Check if cost per night is not empty or zero
        if (accommodation.getCostPerNight() <= 0) {
            throw new IllegalArgumentException("Cost per night must be greater than zero.");
        }

        // Check if photos are not empty and at least one is in a valid format
        if (accommodation.getPhotos() == null || accommodation.getPhotos().length == 0) {
            throw new IllegalArgumentException("At least one valid photo must be provided.");
        }
    }

    // Metodo per validare un oggetto di tipo User
    public static void validateUser(RegisteredUser registerUser) {

        if (registerUser.getUsername() == null || registerUser.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (registerUser.getPassword() == null || registerUser.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (registerUser.getName() == null || registerUser.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (registerUser.getSurname() == null || registerUser.getSurname().isEmpty()) {
            throw new IllegalArgumentException("Surname cannot be null or empty");
        }
        if (registerUser.getBirthPlace() == null || registerUser.getBirthPlace().isEmpty()) {
            throw new IllegalArgumentException("Birth place cannot be null or empty");
        }
        if (registerUser.getEmail() == null || registerUser.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (registerUser.getBirthDate() == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }
        if (registerUser.getAddress() == null || registerUser.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        if (registerUser.getAddressNumber() <= 0) {
            throw new IllegalArgumentException("Address number must be greater than zero");
        }
    }

    // Metodo per validare un oggetto di tipo Book
    public static void validateBook(Book book) {
        // Validate the fields of the Book

        // Check if the occupiedDates is null
        if (book.getOccupiedDates() == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null.");
        }

        // Check that the start date is before the end date
        OccupiedPeriod period = book.getOccupiedDates().get(0);; // Assuming there’s only one period
        if (period.getStart() == null || period.getEnd() == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null.");
        }
        if (!period.getStart().isBefore(period.getEnd())) {
            throw new IllegalArgumentException("Start date must be before the end date.");
        }

        // Validate that the required fields are not empty
        if (book.getUsername() == null || book.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (book.getEmail() == null || book.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (book.getBirthPlace() == null || book.getBirthPlace().isEmpty()) {
            throw new IllegalArgumentException("Birthplace cannot be empty.");
        }
        if (book.getAddress() == null || book.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }

        // Check if guestFirstNames and guestLastNames arrays have the same length
        if (book.getGuestFirstNames() != null && book.getGuestLastNames() != null) {
            if (book.getGuestFirstNames().length != book.getGuestLastNames().length) {
                throw new IllegalArgumentException("The number of guest first names and last names must be the same.");
            }
        }

    }




}

