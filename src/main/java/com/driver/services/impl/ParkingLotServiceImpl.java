package com.driver.services.impl;

import com.driver.exception.ParkingLotNotFoundExcception;
import com.driver.model.SpotType;
import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
            ParkingLot newParkingLot = new ParkingLot();
            newParkingLot.setName(name);
            newParkingLot.setAddress(address);

            ParkingLot savedParkingLot = parkingLotRepository1.save(newParkingLot);

            return savedParkingLot;
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {

        Spot newSpot = new Spot();


        if(numberOfWheels <= 2){
            newSpot.setSpotType(SpotType.TWO_WHEELER);
        } else if (numberOfWheels <=4) {
            newSpot.setSpotType(SpotType.FOUR_WHEELER);
        }else {
            newSpot.setSpotType(SpotType.OTHERS);
        }

        newSpot.setPricePerHour(pricePerHour);
        newSpot.setOccupied(false);


        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).get();
        newSpot.setParkingLot(parkingLot);

        Spot savedSpot = spotRepository1.save(newSpot);

        parkingLot.getSpotList().add(savedSpot);

        parkingLotRepository1.save(parkingLot);


        return  savedSpot;

    }

    @Override
    public void deleteSpot(int spotId) {

        Optional<Spot> spot = spotRepository1.findById(spotId);

        if(spot.isEmpty()){
            return;
        }

        ParkingLot parkingLot = spot.get().getParkingLot();

        parkingLot.getSpotList().remove(spot.get());

        spotRepository1.deleteById(spotId);

    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {

        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).get();


        Spot updatedSpot = null;

        for(Spot spot : parkingLot.getSpotList()){

            if(spot.getId() == spotId){
                updatedSpot = spot;
            }
        }

        updatedSpot.setPricePerHour(pricePerHour);


        return spotRepository1.save(updatedSpot);


    }

    @Override
    public void deleteParkingLot(int parkingLotId) {

        Optional<ParkingLot> deleteparkingLot = parkingLotRepository1.findById(parkingLotId);

        if(deleteparkingLot.isEmpty()){
            throw new ParkingLotNotFoundExcception("Parking lot Doesn't exist");
        }

        List<Spot> spotList = deleteparkingLot.get().getSpotList();

        for(Spot spot : spotList){

            spotRepository1.delete(spot);
        }

        parkingLotRepository1.deleteById(parkingLotId);
    }
}
