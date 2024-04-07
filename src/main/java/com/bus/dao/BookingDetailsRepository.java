package com.bus.dao;

import com.bus.entities.BookingDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingDetailsRepository extends JpaRepository<BookingDetails, Long> {
	List<BookingDetails> findByPaymentStatus(String paymentStatus);
    List<BookingDetails> findByPaymentStatusAndUserEmail(String paymentStatus, String userEmail);

}
