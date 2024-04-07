package com.bus.config;

import com.bus.dao.UserRepository;
import com.bus.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailssServiceImpl implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.getUserByUsername(username);

		if (user == null || !user.isEnabled()) {
			System.out.println("User not found or account is disabled");
			throw new UsernameNotFoundException("Could not find user or user account is disabled");
		}
		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		return customUserDetails;
	}

}