package com.driver.services.impl;

import com.driver.Enum.SpotType;
import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
            ParkingLot newParkingLot = new ParkingLot(name,address);

            ParkingLot savedParkingLot = parkingLotRepository1.save(newParkingLot);

            return savedParkingLot;
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {

        Spot newSpot = new Spot();
        SpotType spotType = null;

        if(numberOfWheels <= 2){
            spotType = spotType.TWO_WHEELER;
        } else if (numberOfWheels <=4) {
            spotType = spotType.FOUR_WHEELER;
        }else {
            spotType = spotType.OTHERS;
        }

        newSpot.setSpotType(spotType);
        newSpot.setPricePerHour(pricePerHour);
        newSpot.setOccupied(false);

        Spot savedSpot = spotRepository1.save(newSpot);

        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).get();

        parkingLot.getSpots().add(savedSpot);

        parkingLotRepository1.save(parkingLot);


        return  savedSpot;

    }

    @Override
    public void deleteSpot(int spotId) {

        spotRepository1.deleteById(spotId);

    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {

        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).get();


        Spot updatedSpot = null;

        for(Spot spot : parkingLot.getSpots()){

            if(spot.getId() == spotId){
                updatedSpot = spot;
            }
        }

        updatedSpot.setPricePerHour(pricePerHour);


        return spotRepository1.save(updatedSpot);


    }

    @Override
    public void deleteParkingLot(int parkingLotId) {

        ParkingLot deleteparkingLot = parkingLotRepository1.findById(parkingLotId).get();

        List<Spot> spotList = deleteparkingLot.getSpots();

        for(Spot spot : spotList){

            spotRepository1.deleteById(spot.getId());
        }

        parkingLotRepository1.deleteById(parkingLotId);
    }
}
