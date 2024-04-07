package com.bus.dao;

import com.bus.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("SELECT u FROM User u Where u.email=:email")
	public User getUserByUsername(@Param("email") String email);
}
