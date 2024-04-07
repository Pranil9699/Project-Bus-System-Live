package com.bus.entities;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "my_order")
public class MyOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myOrderId;

    private String orderId;
    private String amount;
    private String receipt;
    private String status;
    private String paymentId;

    @OneToOne
    @JoinColumn(name = "booking_id", unique = true)
    private BookingDetails bookingDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
}
