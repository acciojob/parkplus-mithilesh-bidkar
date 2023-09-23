package com.driver.services.impl;

import com.driver.model.PaymentMode;
import com.driver.exception.InsufficientAmountException;
import com.driver.model.Payment;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode)  {

        Reservation reservation = reservationRepository2.findById(reservationId).get();

        int bill = reservation.getNumberOfHours()*reservation.getSpot().getPricePerHour();



        if(bill > amountSent){

            throw new InsufficientAmountException("Insufficient Amount");
        }

        Payment payment = new Payment();
        payment.setPaymentCompleted(true);

        String paymentMode = mode.toUpperCase();

        if(paymentMode.equals("CARD")){
            payment.setPaymentMode(PaymentMode.CARD);
        }else if(paymentMode.equals("CASH")){
            payment.setPaymentMode(PaymentMode.CASH);
        } else if (paymentMode.equals("UPI")) {
            payment.setPaymentMode(PaymentMode.UPI);
        }else {
            throw new RuntimeException("Payment mode not detected");
        }

        payment.setReservation(reservation);

        Payment savedPayment = paymentRepository2.save(payment);

        reservation.setPayment(savedPayment);

        reservationRepository2.save(reservation);

        return savedPayment;

    }
}
