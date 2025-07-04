package com.bus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter {

	
	
	@Autowired
	private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	
    @Bean
    public UserDetailsService getUserDetailsService() {
        return new UserDetailssServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/user/**").hasRole("USER")
            .antMatchers("/error/**").permitAll()
            .antMatchers("/**").permitAll()
            .and()
            .formLogin()
                .loginPage("/signin")
                .loginProcessingUrl("/dologin")
                .successHandler(successHandler())
                .failureHandler(customAuthenticationFailureHandler)
                .and()
            .csrf().disable();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

//    @Bean
//    public CustomAuthenticationFailureHandler failureHandler() {
//        return new CustomAuthenticationFailureHandler();
//    }
}