package com.bus.controller;

import com.bus.dao.*;
import com.bus.entities.*;

import com.bus.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class HomeController {

//	@Autowired
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private ForgotPasswordRepository forgotPasswordRepository;

	@Autowired
	private UserRepository userRepository;
//	private 

	@GetMapping(value = { "/", "/home" })
	public String Home(Model m) {

		return "index";
	}

	@GetMapping("/signin")
	public String login(Model model, HttpSession session) {
		String error = (String) session.getAttribute("error");
		if (error != null) {
			model.addAttribute("error", error);
			// Remove the error attribute from the session
			session.removeAttribute("error");
		}
		return "login";
	}

	@GetMapping("/register")
	public String register(Model m) {

		return "reg";
	}

	@GetMapping(value = { "/login-error", "/error" })
	public String loginerror() {
		return "login-error";
	}

	@GetMapping("/about")
	public String about() {
		return "about";
	}

	@PostMapping("/do_register")
	public String doRegister(@ModelAttribute("user") User user, HttpSession session) {
		try {

			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setEnabled(true);
			user.setRole("ROLE_USER");
			User result = userRepository.save(user);
			session.setAttribute("message", new Message("SuccessFully Registered !!", "alert-success"));
			System.out.println(result.toString());

			return "reg";
		} catch (DataIntegrityViolationException e) {
			System.out.println(e);
			session.setAttribute("message", new Message("Email is already taken", "alert-danger"));
			return "reg";

		} catch (Exception e) {
			System.out.println(e);
			session.setAttribute("message", new Message("Somthing went wrong", "alert-danger"));
			return "reg";
		}
	}

	@GetMapping("/forgotPassword")
	public String showForgotPasswordForm(Model model) {
		// Add any necessary attributes to the model
		return "forgot"; // Return the name of the Thymeleaf template for the forgot password form
	}

	@PostMapping("/forgotPassword/do_checkemail")
	public String do_checkemail(@RequestParam("email") String email, Model model, HttpSession session) {

		User user = userRepository.getUserByUsername(email);
		if (user != null) {

			// create the OTP
			Random random = new Random();
			StringBuilder otp = new StringBuilder(6);
			for (int i = 0; i < 6; i++) {
				otp.append(random.nextInt(10));
			}
			int OTP = Integer.parseInt(otp.toString());
			System.out.println("OTP :" + OTP);

//	        /sending Email OTP to user email
			boolean check = EmailService.sendEmail(email, OTP);
			System.out.println("Check:" + check);
			if (check) {

				ForgotPassword save = forgotPasswordRepository.save(new ForgotPassword(email, OTP));
				System.out.println(save + "From DB");
				session.setAttribute("message", new Message("Email Is Sended to " + email, "alert-success"));

			} else {
				session.setAttribute("message",
						new Message("Pleased Try Again, Email is Not Sended!!", "alert-danger"));
				return "redirect:/forgotPassword";

			}

		} else {
			System.out.println("Apsend");
			session.setAttribute("message", new Message("Email-Id is Wrong !!", "alert-danger"));
			return "redirect:/forgotPassword";
		}
		model.addAttribute("title", "Forgot Password(Email) OTP page");
		model.addAttribute("user", user);
		return "userOTP";
	}

	@PostMapping("/do_checkemailAndotp")
	public String do_checkemailAndotp(@RequestParam("email") String email, @RequestParam("otp") String Sotp,
			Model model, HttpSession session) {

		System.out.println(email + " : " + Sotp);
		int otp = Integer.parseInt(Sotp);
		ForgotPassword first = new ForgotPassword(email, otp);
		ForgotPassword byId = forgotPasswordRepository.getByEmailAndOtp(email, otp);
		System.out.println(byId + " : " + first);
		if (first.getEmail().toLowerCase().equals(byId.getEmail()) || first.getOtp() == byId.getOtp()) {
			System.out.println("Matched");
			model.addAttribute("user", userRepository.getUserByUsername(email));
		} else {
			session.setAttribute("message", new Message("OTP is Not Matched", "alert-danger"));
			return "redirect:/forgotPassword";
		}

		return "resetpassword";
	}

	@PostMapping("/do_resetpassword")
	public String do_resetpassword(@RequestParam("email") String email, @RequestParam("newpassword") String newPassword,
			Model model, HttpSession session) {

		if (newPassword.length() >= 6) {
			User user = userRepository.getUserByUsername(email);
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
			session.setAttribute("message", new Message("Password is Updated", "alert-success"));
			forgotPasswordRepository.deleteById(email);
			return "redirect:/signin";
		} else {
			session.setAttribute("message", new Message("Password Must upTo 6 Characters", "alert-danger"));
			return "redirect:/forgotPassword";
		}

	}
}
