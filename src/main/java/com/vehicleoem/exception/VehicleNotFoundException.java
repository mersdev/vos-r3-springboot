package com.vehicleoem.exception;

public class VehicleNotFoundException extends BusinessException {
    public VehicleNotFoundException(String vin) {
        super("VEHICLE_NOT_FOUND", "Vehicle not found with VIN: " + vin);
    }
}
