//package com.bus.entities;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//
//import javax.persistence.*;
//import java.time.LocalDate;
//
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "booking_details")
//public class BookingDetails {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "userId")
//    private User user;
//
//    @Column(name = "origin")
//    private String origin;
//
//
//	@Column(name = "destination")
//    private String destination;
//
//    @Column(name = "date")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//    private LocalDate date;
//
//    @Column(name = "total_amount")
//    private double totalAmount;
//
//    @Column(name = "payment_status")
//    private String paymentStatus;
//
//    @Column(name = "tickets")
//    private int tickets;
//
//    // Getters and setters
//}

package com.bus.entities;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
        import java.time.LocalDate;

@Entity
@Data
@Table(name = "booking_details")
public class BookingDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "date", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "tickets", nullable = false)
    private int tickets;
}

