package com.driver.exception;

public class ParkingLotNotFoundExcception extends RuntimeException{

    public ParkingLotNotFoundExcception(String message){
        super(message);
    }
}
