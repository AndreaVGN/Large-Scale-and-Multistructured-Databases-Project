package com.example.WanderHub.demo.config;

public class Spazzatura {

    /*
    public String lockHouse(ObjectId accommodationId, String start, String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate inizio = LocalDate.parse(start, formatter);
        if (inizio.isBefore(today)) {
            throw new RuntimeException("periodo di tempo non valido.");
        }
        LocalDate fine = LocalDate.parse(end, formatter);
        if (fine.isAfter(today.plusYears(1))) {
            throw new RuntimeException("periodo di tempo non valido.");
        }
        if(inizio.isAfter(fine)){
            throw new RuntimeException("periodo di tempo non valido.");
        }

        String lockKey = "booking:accId:" + accommodationId + ":start:" + start + ":end:" + end;

        // Tentiamo di acquisire il lock utilizzando SETNX (Set if Not Exists)

        if (redisUtility.lock(lockKey) == null || !redisUtility.lock(lockKey)) {
            return null; // Impossibile acquisire il lock
        }

        // Se c'è una sovrapposizione, rilascia subito il lock e ritorna false
        if (redisUtility.isOverlappingBooking(accommodationId, start, end)) {
            redisUtility.delete(lockKey); // Rilascia il lock subito
            return null;
        }

        String timestamp = String.valueOf(System.currentTimeMillis());

        redisUtility.setKey(lockKey, timestamp, lockTTL);

        return timestamp; // Restituisce il timestamp generato
    }

    public boolean lockHouseReg(ObjectId accommodationId, String username, String start, String end) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate startDate = LocalDate.parse(start, formatter);

        if (startDate.isBefore(today)) {
            throw new RuntimeException("periodo di tempo non valido.");
        }

        LocalDate endDate = LocalDate.parse(end, formatter);
        if (endDate.isAfter(today.plusYears(1))) {
            throw new RuntimeException("periodo di tempo non valido.");
        }

        if(startDate.isAfter(endDate)){
            throw new RuntimeException("periodo di tempo non valido.");
        }

        accommodationRepository.findByAccommodationId(accommodationId)
                .orElseThrow(() -> new RuntimeException("Accommodation not found"));

        String lockKey = "booking:accId:" + accommodationId + ":start:" + start + ":end:" + end;

        // Tentiamo di acquisire il lock utilizzando SETNX
        if (redisUtility.lock(lockKey) == null || !redisUtility.lock(lockKey)) {
            return false; // Impossibile acquisire il lock
        }

        // Se c'è una sovrapposizione, rilascia subito il lock e ritorna false
        if (redisUtility.isOverlappingBooking(accommodationId, start, end)) {
            System.out.println("Lock not acquired per sovrapposizione!");
            redisUtility.delete(lockKey);; // Rilascia il lock subito
            return false;
        }

        redisUtility.setKey(lockKey, username,lockTTL);

        return true;
    }
*/

    /*
    public boolean unlockHouse(ObjectId houseId, String start, String end, String timestampCookie) {
        String lockKey = "booking:accId:" + houseId + ":start:" + start + ":end:" + end;

        String storedTimestamp = redisUtility.getValue(lockKey);

        if (storedTimestamp != null && storedTimestamp.equals(timestampCookie)) {
            redisUtility.delete(lockKey);
            return true;
        }

        return false;
    }

    public boolean unlockHouseReg(ObjectId houseId, String username, String start, String end) {
        String lockKey = "booking:accId:" + houseId + ":start:" + start + ":end:" + end;

        String storedUsername = redisUtility.getValue(lockKey);

        if (storedUsername != null && storedUsername.equals(username)) {
            redisUtility.delete(lockKey);
            return true;
        }

        return false;
    }
*/
}
