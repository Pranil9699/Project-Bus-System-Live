package com.bus.dao;

import com.bus.entities.MyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyOrderRepository extends JpaRepository<MyOrder, Long> {
	
	MyOrder findByOrderId(String orderId);
	MyOrder findByBookingDetails_Id(Long bookingId);


}
