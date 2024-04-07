//package com.fiza.lal_pari.controller;
//
//import java.security.Principal;
//import java.time.LocalDate;
//
//import java.util.*;
//
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.fiza.lal_pari.dao.*;
//import com.fiza.lal_pari.entities.*;
//import com.razorpay.Order;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//
//@Controller
//@RequestMapping("/user")
//public class UserController {
//	@Autowired
//	private UserRepository userRepository;
//
//	@Autowired
//	private TravelCostRepository travelCostRepository;
//
//	@Autowired
//	private BookingDetailsRepository bookingDetailsRepository;
//
//	@Autowired
//	private MyOrderRepository myOrderRepository;
//
//	@GetMapping(value = { "/index", "/" })
//	public String dashboard(Model model, Principal principal) {
//
//		model.addAttribute("title", "Normal User dashboard");
//		return "normal/user_dashboard";
//	}
//
//	@GetMapping("/about")
//	public String about() {
//		return "normal/about";
//	}
//
////	@GetMapping("/booking")
////	public String booking() {
////		return "normal/booking";
////	}
//
//	@GetMapping("/contact")
//	public String contact() {
//		return "normal/contact";
//	}
//
//	@GetMapping("/payment")
//	public String payment() {
//		return "normal/payment";
//	}
//
//	@GetMapping("/reward")
//	public String reward() {
//		return "normal/reward";
//	}
//
//	@GetMapping("/logout")
//	public String logout() {
//		// Perform logout logic
//		return "redirect:/";
//	}
//
//	@GetMapping("/profile")
//	public String userProfile(Model model, Principal principal) {
//		String email = principal.getName();
//		User user = userRepository.getUserByUsername(email);
//		model.addAttribute("user", user);
//		return "normal/profile";
//	}
//
//	@PostMapping("/profile/update")
//	public String updateUser(@ModelAttribute("user") User updatedUser) {
//		// Retrieve the existing user from the database
//		User existingUser = userRepository.findById(updatedUser.getUserId()).orElse(null);
//		if (existingUser == null) {
//			// Handle the case where the user does not exist
//			return "redirect:/user/profile";
//		}
//
//		// Update only the fields that have changed
//		if (!existingUser.getFullName().equals(updatedUser.getFullName())) {
//			existingUser.setFullName(updatedUser.getFullName());
//		}
//
//		if (!existingUser.getGender().equals(updatedUser.getGender())) {
//			existingUser.setGender(updatedUser.getGender());
//		}
//		if (!existingUser.getPhoneNumber().equals(updatedUser.getPhoneNumber())) {
//			existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
//		}
//
//		// Save the updated user
//		User savedUser = userRepository.save(existingUser);
//		System.out.println(savedUser.toString());
//
//		return "redirect:/user/profile";
//	}
//
//	@PostMapping("/profile/delete")
//	public String deleteUser(Principal principal) {
//		String email = principal.getName();
//		User user = userRepository.getUserByUsername(email);
//		userRepository.delete(user);
//		return "redirect:/logout";
//	}
//
//	@GetMapping("/booking")
//	public String getBookingDetails(Model model) {
//		List<TravelCost> travelCosts = travelCostRepository.findAll();
//		Set<String> originCitiesSet = new HashSet<>();
//		Set<String> destinationCitiesSet = new HashSet<>();
//		for (TravelCost travelCost : travelCosts) {
//			originCitiesSet.add(travelCost.getOriginCity());
//			destinationCitiesSet.add(travelCost.getDestinationCity());
//		}
//		List<String> originCities = new ArrayList<>(originCitiesSet);
//		List<String> destinationCities = new ArrayList<>(destinationCitiesSet);
//		model.addAttribute("originCities", originCities);
//		model.addAttribute("destinationCities", destinationCities);
//		return "normal/booking";
//	}
//
//	@PostMapping("/doBooking")
//	public String doBooking(Principal principal, @RequestParam("origin") String origin,
//			@RequestParam("destination") String destination,
//			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//			@RequestParam("tickets") int tickets, Model model) {
//
//		// Validate that the date is not in the past
//		if (date.isBefore(LocalDate.now())) {
//			model.addAttribute("errorMessage", "Please select a future date.");
//			return "normal/booking";
//		}
//
//		String email = principal.getName();
//		User user = userRepository.getUserByUsername(email);
//
//		BookingDetails bookingDetails = new BookingDetails();
//		bookingDetails.setUser(user);
//		bookingDetails.setOrigin(origin);
//		bookingDetails.setDestination(destination);
//		bookingDetails.setDate(date);
//		bookingDetails.setTickets(tickets);
//
//		try {
//			TravelCost travelCost = travelCostRepository.findByOriginCityIgnoreCaseAndDestinationCityIgnoreCase(origin,
//					destination);
//            System.out.println(travelCost.toString());
//			// Check if the TravelCost object is not null
//			if (travelCost != null) {
//				// Calculate the total amount based on the payment for one person and number of
//				// tickets
//				double totalAmount = travelCost.getPaymentForOnePerson() * bookingDetails.getTickets();
//				bookingDetails.setTotalAmount(totalAmount);
//				bookingDetails.setPaymentStatus("Pending");
//
//				BookingDetails save = bookingDetailsRepository.save(bookingDetails);
//
//				model.addAttribute("totalAmount", totalAmount);
//				model.addAttribute("booking_id", save.getId());
//
//				return "normal/payment";
//			} else {
//				// Handle the case where the TravelCost object is not found
//				model.addAttribute("error", "No travel cost found for the given origin and destination cities.");
//				return "normal/booking";
//			}
//		} catch (Exception e) {
//			// Handle exceptions, e.g., database errors
//			model.addAttribute("error", "An error occurred. Please try again later.");
//			return "normal/booking";
//		}
//
//	}
//
//	@PostMapping("/create_order")
//	@ResponseBody
//	public String createOrder(@RequestBody Map<String, String> data, Principal principal) throws RazorpayException {
//		System.out.println("hi");
//
//		String amountStr = data.get("amount");
//
//		System.out.println("bye");
//		double amount = Double.parseDouble(amountStr);
//		int amountInPaisa = (int) (amount);
//
//		String bookingId = data.get("bookingId");
//
//		var client = new RazorpayClient("rzp_test_QFvMW8KhGXxtl9", "A8WZvxnp2EMf2vuJFBj1UnbU");
//		System.out.println("midd");
//		JSONObject object = new JSONObject();
//		object.put("amount", amountInPaisa); // Convert amount to paisa
//		object.put("currency", "INR");
//		object.put("receipt", "txn_123456"); // Generate a unique receipt ID for each transaction
//
//		Order order = client.orders.create(object);
//		System.out.println("Order: " + order);
//
//		// Save the order in your database along with the booking ID
//		User user = userRepository.getUserByUsername(principal.getName());
//		MyOrder myOrder = new MyOrder();
//		myOrder.setOrderId(order.get("id").toString());
//		myOrder.setAmount(amountStr);
//		myOrder.setReceipt(order.get("receipt"));
//		myOrder.setStatus("created");
//		myOrder.setUser(user);
//
//		myOrderRepository.save(myOrder);
//
//		return order.toString();
//	}
//
//	@PostMapping("/update_order")
//	public ResponseEntity<?> updateOrder(@RequestBody Map<String, String> data) {
//		String paymentId = data.get("paymentId");
//		String orderId = data.get("orderId");
//		String status = data.get("status");
//		Integer booking_id = Integer.parseInt(data.get("b"));
//		System.out.println(booking_id);
//		// Update the payment status in your database using the payment ID and order ID
//		MyOrder myOrder = myOrderRepository.findByOrderId(orderId);
//		if (myOrder != null) {
//			myOrder.setPaymentId(paymentId);
//			myOrder.setStatus(status);
//			myOrderRepository.save(myOrder);
//
//			BookingDetails bookingDetails = bookingDetailsRepository.findById((long) booking_id).get();
//
//			bookingDetails.setPaymentStatus("complete");
//
//			bookingDetailsRepository.save(bookingDetails);
//			return ResponseEntity.ok(Map.of("message", "Payment Successfully Done And Updated"));
//		} else {
//			return ResponseEntity.badRequest().body(Map.of("message", "Order not found"));
//		}
//	}
//
//}

package com.bus.controller;

import com.bus.dao.*;
import com.bus.entities.*;
import com.bus.utils.PDFGenerator;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TravelCostRepository travelCostRepository;

	@Autowired
	private BookingDetailsRepository bookingDetailsRepository;

	@Autowired
	private MyOrderRepository myOrderRepository;

	@Autowired
	private FeedbackRepository feedbackRepository;

	@GetMapping(value = { "/index", "/", "" })
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "Normal User dashboard");
		return "normal/user_dashboard";
	}

	@GetMapping("/about")
	public String about() {
		return "normal/about";
	}

	@GetMapping("/contact")
	public String contact() {
		return "normal/contact";
	}

	@GetMapping("/payment")
	public String payment() {
		return "normal/payment";
	}

	@GetMapping("/reward")
	public String reward() {
		return "normal/reward";
	}

	@GetMapping("/logout")
	public String logout() {
		// Perform logout logic
		return "redirect:/";
	}

	@GetMapping("/profile")
	public String userProfile(Model model, Principal principal) {
		String email = principal.getName();
		User user = userRepository.getUserByUsername(email);
		model.addAttribute("user", user);
		return "normal/profile";
	}

	@PostMapping("/profile/update")
	public String updateUser(@ModelAttribute("user") User updatedUser) {
		// Retrieve the existing user from the database
		User existingUser = userRepository.findById(updatedUser.getUserId()).orElse(null);
		if (existingUser == null) {
			// Handle the case where the user does not exist
			return "redirect:/user/profile";
		}

		// Update only the fields that have changed
		if (!existingUser.getFullName().equals(updatedUser.getFullName())) {
			existingUser.setFullName(updatedUser.getFullName());
		}

		if (!existingUser.getGender().equals(updatedUser.getGender())) {
			existingUser.setGender(updatedUser.getGender());
		}
		if (!existingUser.getPhoneNumber().equals(updatedUser.getPhoneNumber())) {
			existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
		}

		// Save the updated user
		User savedUser = userRepository.save(existingUser);
		System.out.println(savedUser.toString());

		return "redirect:/user/profile";
	}

	@PostMapping("/profile/delete")
	public String deleteUser(Principal principal) {
		String email = principal.getName();
		User user = userRepository.getUserByUsername(email);
		user.setEnabled(false); // Update user's status to disabled
		userRepository.save(user);
		return "redirect:/logout";
	}

	@GetMapping("/booking")
	public String getBookingDetails(Model model) {
		List<TravelCost> travelCosts = travelCostRepository.findAll();
		Set<String> originCitiesSet = new HashSet<>();
		Set<String> destinationCitiesSet = new HashSet<>();
		for (TravelCost travelCost : travelCosts) {
			originCitiesSet.add(travelCost.getOriginCity());
			destinationCitiesSet.add(travelCost.getDestinationCity());
		}
		List<String> originCities = new ArrayList<>(originCitiesSet);
		List<String> destinationCities = new ArrayList<>(destinationCitiesSet);
		model.addAttribute("originCities", originCities);
		model.addAttribute("destinationCities", destinationCities);
		return "normal/booking";
	}

	@PostMapping("/doBooking")
	public String doBooking(Principal principal, @RequestParam("origin") String origin,
			@RequestParam("destination") String destination,
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam("tickets") int tickets, Model model) {

		// Validate that the date is not in the past
		if (date.isBefore(LocalDate.now())) {
			model.addAttribute("errorMessage", "Please select a future date.");
			return "normal/booking";
		}

		String email = principal.getName();
		User user = userRepository.getUserByUsername(email);

		BookingDetails bookingDetails = new BookingDetails();
		bookingDetails.setUser(user);
		bookingDetails.setOrigin(origin);
		bookingDetails.setDestination(destination);
		bookingDetails.setDate(date);
		bookingDetails.setTickets(tickets);

		try {
			TravelCost travelCost = travelCostRepository.findByOriginCityIgnoreCaseAndDestinationCityIgnoreCase(origin,
					destination);
//            System.out.println(travelCost.toString());
			// Check if the TravelCost object is not null
			if (travelCost != null) {
				// Calculate the total amount based on the payment for one person and number of
				// tickets
				double totalAmount = travelCost.getPaymentForOnePerson() * bookingDetails.getTickets();
				bookingDetails.setTotalAmount(totalAmount);
				bookingDetails.setPaymentStatus("Pending");

				BookingDetails save = bookingDetailsRepository.save(bookingDetails);

				model.addAttribute("totalAmount", totalAmount);
				model.addAttribute("booking_id", save.getId());

				return "normal/payment";
			} else {
				// Handle the case where the TravelCost object is not found
				model.addAttribute("error", "No travel cost found for the given origin and destination cities.");
				return "normal/booking";
			}
		} catch (Exception e) {
			// Handle exceptions, e.g., database errors
			model.addAttribute("error", "An error occurred. Please try again later.");
			return "normal/booking";
		}

	}

	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, String> data, Principal principal) throws RazorpayException {

		String amountStr = data.get("amount");

		double amount = Double.parseDouble(amountStr);
		int amountInPaisa = (int) (amount);

		var client = new RazorpayClient("rzp_test_QFvMW8KhGXxtl9", "A8WZvxnp2EMf2vuJFBj1UnbU");
		System.out.println("midd");
		JSONObject object = new JSONObject();
		object.put("amount", amountInPaisa); // Convert amount to paisa
		object.put("currency", "INR");
		object.put("receipt", "txn_123456"); // Generate a unique receipt ID for each transaction

		Order order = client.orders.create(object);
		System.out.println("Order: " + order);

		// Save the order in your database along with the booking ID
		User user = userRepository.getUserByUsername(principal.getName());
		MyOrder myOrder = new MyOrder();
		myOrder.setOrderId(order.get("id").toString());
		myOrder.setAmount((amountInPaisa / 100) + "");
		myOrder.setReceipt(order.get("receipt"));
		myOrder.setStatus("created");
		myOrder.setUser(user);

		myOrderRepository.save(myOrder);

		return order.toString();
	}

	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, String> data) {
		String paymentId = data.get("paymentId");
		String orderId = data.get("orderId");
		String status = data.get("status");
		Integer booking_id = Integer.parseInt(data.get("b"));
		System.out.println(booking_id);
		// Update the payment status in your database using the payment ID and order ID
		MyOrder myOrder = myOrderRepository.findByOrderId(orderId);
		if (myOrder != null) {
			myOrder.setPaymentId(paymentId);
			myOrder.setStatus(status);
			myOrder.setBookingDetails(bookingDetailsRepository.findById((long) booking_id).get());
			myOrderRepository.save(myOrder);

			BookingDetails bookingDetails = bookingDetailsRepository.findById((long) booking_id).get();

			bookingDetails.setPaymentStatus("complete");

			bookingDetailsRepository.save(bookingDetails);
			return ResponseEntity.ok(Map.of("message", "Payment Successfully Done And Updated"));
		} else {
			return ResponseEntity.badRequest().body(Map.of("message", "Order not found"));
		}
	}

	@PostMapping("/contact")
	public String contactFormSubmit(@RequestParam("fullName") String fullName, @RequestParam("email") String email,
			@RequestParam("content") String content, Model model) {
		// Process the form data (e.g., send an email, save to database, etc.)
		// For demonstration purposes, we'll just print the form data to the console
		System.out.println("Full Name: " + fullName);
		System.out.println("Email: " + email);
		System.out.println("Message: " + content);

		// Add a success message to be displayed on the contact page
		model.addAttribute("message", "Your message has been sent successfully!");

		// Redirect back to the contact page
		return "redirect:/user/contact";
	}

	@PostMapping("/feedback")
	public String submitFeedback(@RequestParam("ratingContent") String ratingContent, Principal principal,
			Model model) {

		User user = userRepository.getUserByUsername(principal.getName());

		// Check if the user has existing feedback
		Optional<Feedback> optional = null;
		Feedback existingFeedback=null;
		System.out.println(user.toString());
		try {
			optional = feedbackRepository.findById(user.getUserId());
			existingFeedback=optional.get();
			System.out.println("Done");
		} catch (Exception e) {
			// Handle exception
//			System.out.println(e.getMessage());
		}

		if (existingFeedback != null) {
			// Update existing feedback
			System.out.println("here");
			existingFeedback.setRatingContent(ratingContent);
			feedbackRepository.save(existingFeedback);
		} else {
			// Create new feedback
			System.out.println("Done");
			Feedback feedback = new Feedback();
			feedback.setId(user.getUserId());
			System.out.println("Done");
			feedback.setRatingContent(ratingContent);
			System.out.println("Done");
			feedbackRepository.save(feedback);
			System.out.println("Done");
		}

		// Add success message
		model.addAttribute("message", "Feedback submitted successfully!");

		// Redirect to feedback page
		return "redirect:/user/feedback";
	}

	@GetMapping("/payments")
	public String showPayments(Model model, Principal principal) {
		String email = principal.getName();

		// Fetch payment statuses for the current user only
		List<BookingDetails> pendingPayments = bookingDetailsRepository.findByPaymentStatusAndUserEmail("pending",
				email);
		List<BookingDetails> completePayments = bookingDetailsRepository.findByPaymentStatusAndUserEmail("complete",
				email);
		// Add payment statuses to the model
		model.addAttribute("pendingPayments", pendingPayments);
		model.addAttribute("completePayments", completePayments);

		return "normal/payments"; // Assuming "payments" is the name of your Thymeleaf template
	}

	@GetMapping("/editBooking/{id}")
	public String editBooking(@PathVariable("id") Long id, Model model) {
		// Fetch booking details by ID
		Optional<BookingDetails> optionalBookingDetails = bookingDetailsRepository.findById(id);
		if (optionalBookingDetails.isPresent()) {
			BookingDetails bookingDetails = optionalBookingDetails.get();
			model.addAttribute("bookingDetails", bookingDetails);

			// Fetch travel costs for dropdowns
			List<TravelCost> travelCosts = travelCostRepository.findAll();
			Set<String> originCitiesSet = new HashSet<>();
			Set<String> destinationCitiesSet = new HashSet<>();
			for (TravelCost travelCost : travelCosts) {
				originCitiesSet.add(travelCost.getOriginCity());
				destinationCitiesSet.add(travelCost.getDestinationCity());
			}
			List<String> originCities = new ArrayList<>(originCitiesSet);
			List<String> destinationCities = new ArrayList<>(destinationCitiesSet);
			model.addAttribute("originCities", originCities);
			model.addAttribute("destinationCities", destinationCities);

			return "normal/editBooking"; // Assuming "editBooking" is the name of your Thymeleaf template
		} else {
			// Booking not found, handle this case (e.g., show an error message)
			return "redirect:/user/payments";
		}
	}

	@PostMapping("/updateBooking")
	public String updateBooking(@ModelAttribute("bookingDetails") BookingDetails bookingDetails, Model model) {
		try {
			BookingDetails currentBooking = bookingDetailsRepository.findById(bookingDetails.getId()).orElse(null);

			if (currentBooking != null) {
				TravelCost travelCost = travelCostRepository.findByOriginCityIgnoreCaseAndDestinationCityIgnoreCase(
						bookingDetails.getOrigin(), bookingDetails.getDestination());
				if (travelCost != null) {
					double totalAmount = travelCost.getPaymentForOnePerson() * bookingDetails.getTickets();
					bookingDetails.setTotalAmount(totalAmount);
				} else {
					model.addAttribute("error", "No travel cost found for the given origin and destination cities.");
					// Return to the edit form with an error message
					return "normal/editBooking";
				}

				currentBooking.setOrigin(bookingDetails.getOrigin());
				currentBooking.setDestination(bookingDetails.getDestination());
				currentBooking.setDate(bookingDetails.getDate());
				currentBooking.setTickets(bookingDetails.getTickets());
				currentBooking.setTotalAmount(bookingDetails.getTotalAmount());

				bookingDetailsRepository.save(currentBooking);

				return "redirect:/user/payments";
			} else {
				model.addAttribute("error", "Booking not found.");
				return "normal/editBooking";
			}
		} catch (Exception e) {
			model.addAttribute("error", "An error occurred. Please try again later.");
			return "normal/editBooking";
		}
	}

	@GetMapping("/deleteBooking/{id}")
	public String deleteBooking(@PathVariable("id") Long id, Model model) {
		try {
			bookingDetailsRepository.deleteById(id);
		} catch (Exception e) {
			// Handle exceptions, e.g., database errors
			System.out.println(e.getMessage());
			model.addAttribute("error", "Unable to delete booking.");
			return "redirect:/user/payments";
		}
		return "redirect:/user/payments";
	}

	@GetMapping("/BookingChecking/{id}")
	public String showPaymentPage(@PathVariable("id") Long bookingId, Model model) {
		BookingDetails bookingDetails = bookingDetailsRepository.findById(bookingId).orElse(null);
		if (bookingDetails != null) {
			model.addAttribute("totalAmount", bookingDetails.getTotalAmount());
			model.addAttribute("booking_id", bookingDetails.getId());

			return "normal/payment";
		} else {
			model.addAttribute("error", "Booking details not found.");
			return "normal/booking";
		}
	}

	@GetMapping("/downloadTicket/{id}")
	public ResponseEntity<InputStreamResource> downloadTicket(@PathVariable("id") Long bookingId) {
		BookingDetails bookingDetails = bookingDetailsRepository.findById(bookingId).orElse(null);
		MyOrder findByBookingDetails_Id = myOrderRepository.findByBookingDetails_Id(bookingId);
		System.out.println(findByBookingDetails_Id.toString());
		if (bookingDetails == null) {
			System.out.println("here");
			return ResponseEntity.notFound().build();
		}

		ByteArrayInputStream bis = PDFGenerator.bookingDetailsPDFReport(bookingDetails, findByBookingDetails_Id);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=bookings.pdf");

		return ResponseEntity.ok().headers(headers).contentType(org.springframework.http.MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));
	}

}
