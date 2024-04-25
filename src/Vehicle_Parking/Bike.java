package Vehicle_Parking;

import java.time.LocalDateTime;

class Car {
    private String plateNumber;
    private LocalDateTime entryTime;

    // Constructor
    public Car(String plateNumber) {
        this.plateNumber = plateNumber;
        // Initialize entryTime when a car is created
        this.entryTime = LocalDateTime.now();
    }
    public String getPlateNumber() {
        return plateNumber;
    }


    public LocalDateTime getEntryTime() {
        return entryTime;
    }

}

public class Bike {
    private String plateNumber;
    private LocalDateTime entryTime;

    // Constructor
    public Bike(String plateNumber) {
        this.plateNumber = plateNumber;
        // Initialize entryTime when a bike is created
        this.entryTime = LocalDateTime.now();
    }
    public String getPlateNumber() {
        return plateNumber;
    }
    public LocalDateTime getEntryTime() {
        return entryTime;
    }
}

