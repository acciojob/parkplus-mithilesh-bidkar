package com.driver.services.impl;

import com.driver.Enum.SpotType;
import com.driver.exception.ParkingLotNotFoundExcception;
import com.driver.exception.SpotNotAvailableException;
import com.driver.exception.userNotFoundException;
import com.driver.model.ParkingLot;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.model.User;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {

        Optional<User> user = userRepository3.findById(userId);

        if(user.isEmpty()){

            throw  new userNotFoundException("Cannot make reservation");
        }

        Optional<ParkingLot> parkingLot = parkingLotRepository3.findById(parkingLotId);

        if(parkingLot.isEmpty()){

            throw new ParkingLotNotFoundExcception("Cannot make reservation");

        }


        int minimumCost = Integer.MAX_VALUE;

        Spot requiredSpot = null;

        List<Spot> spotList = parkingLot.get().getSpots();

        for(Spot spot : spotList){

            int thisSpotWheels = 0;

            if(spot.getSpotType() == SpotType.TWO_WHEELER){
                thisSpotWheels = 2;
            } else if (spot.getSpotType() == SpotType.FOUR_WHEELER) {
                thisSpotWheels = 4;
            }else {
                thisSpotWheels = 10;
            }

            if(spot.isOccupied() == false &&  numberOfWheels <= thisSpotWheels  ){

                if(minimumCost>spot.getPricePerHour()){
                    minimumCost = spot.getPricePerHour();
                    requiredSpot = spot;
                }
            }
        }

        if (requiredSpot == null){
            throw new SpotNotAvailableException("Cannot make reservation");
        }

        requiredSpot.setOccupied(true);


        Reservation reservation = new Reservation();

        reservation.setSpot(requiredSpot);
        reservation.setUser(user.get());
        reservation.setNumberOfHours(timeInHours);

        Reservation savedReservation = reservationRepository3.save(reservation);

        requiredSpot.getReservations().add(savedReservation);

        spotRepository3.save(requiredSpot);

        return savedReservation;

    }
}
