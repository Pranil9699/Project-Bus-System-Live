package com.bus.entities;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "users") // Specify the table name here
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(nullable = false)
	private String fullName;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String role;

	private boolean enabled;

	private String gender;

	private String phoneNumber;
}
