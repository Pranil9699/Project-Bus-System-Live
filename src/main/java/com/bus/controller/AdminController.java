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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TravelCostRepository travelCostRepository;

	@Autowired
	private BookingDetailsRepository bookingDetailsRepository;

	@Autowired
	private MyOrderRepository myOrderRepository;

	@GetMapping("/about")
	public String about() {
		return "admin/about";
	}

	@GetMapping(value = {"/index","/",""})
	public String dashboard(Model model, Principal principal) {

		model.addAttribute("title", "Admin dashboard");
		return "admin/admin_dashboard";
	}

	@GetMapping("/user")
	public String users(Model model, Principal principal) {
	    List<User> users = userRepository.findAll(); // Assuming you have a method in your UserRepository to get all users
	    users = users.stream()
	                 .filter(user -> !user.getRole().contains("ROLE_ADMIN")) // Assuming getRoles() returns a list of user roles
	                 .collect(Collectors.toList());
	    model.addAttribute("title", "Admin dashboard");
	    model.addAttribute("users", users);
	    return "admin/user";
	}

	@PostMapping("/changeStatus")
    public String changeUserStatus(@RequestParam("userId") Long userId,
                                    @RequestParam("action") String action) {
        // Find the user by userId from the database
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            // Handle error, user not found
            return "redirect:/admin/user";
        }

        // Update the user's enable status based on the action
        if (action.equals("block")) {
            user.setEnabled(false);
        } else if (action.equals("unblock")) {
            user.setEnabled(true);
        }

        // Save the updated user
        userRepository.save(user);

        return "redirect:/admin/user";
    }
	
	 @PostMapping("/deleteUser")
	    public String deleteUser(@RequestParam("userId") Long userId) {
	        userRepository.deleteById(userId);
	        return "redirect:/admin/user"; // Redirect to the user management page
	    }
	 
	
	 @PostMapping("/doUpdateUser")
	    public String updateUser( @ModelAttribute("user") User updatedUser, Model model) {
	        // Find the user by ID
	        User user = userRepository.findById(updatedUser.getUserId())
	                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
	        System.out.println(updatedUser.toString());

	        // Update the user details
	        user.setFullName(updatedUser.getFullName());
	        user.setEmail(updatedUser.getEmail());
	        user.setGender(updatedUser.getGender());
	        user.setPhoneNumber(updatedUser.getPhoneNumber());

	        // Save the updated user
	        userRepository.save(user);

	        // Redirect back to the user list page
	        return "redirect:/admin/user";
	    }
	 
	 @GetMapping("/payments")
		public String showPayments(Model model) {
			
			List<BookingDetails> pendingPayments = bookingDetailsRepository.findByPaymentStatus("pending");
			List<BookingDetails> completePayments = bookingDetailsRepository.findByPaymentStatus("complete");
			// Add payment statuses to the model
			model.addAttribute("pendingPayments", pendingPayments);
			model.addAttribute("completePayments", completePayments);

			return "admin/payments"; // Assuming "payments" is the name of your Thymeleaf template
		}
	 
	 @GetMapping("/orderHistory")
	    public String orderHistory(Model model) {
	        // Get all orders from the repository
	        Iterable<MyOrder> orders = myOrderRepository.findAll();
	        
	        // Add orders to the model
	        model.addAttribute("orders", orders);
	        
	        // Return the view name
	        return "admin/orders";
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
			myOrder.setAmount((amountInPaisa/100)+"");
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
				myOrder.setBookingDetails(bookingDetailsRepository.findById((long)booking_id).get());
				myOrderRepository.save(myOrder);

				BookingDetails bookingDetails = bookingDetailsRepository.findById((long) booking_id).get();

				bookingDetails.setPaymentStatus("complete");

				bookingDetailsRepository.save(bookingDetails);
				return ResponseEntity.ok(Map.of("message", "Payment Successfully Done And Updated"));
			} else {
				return ResponseEntity.badRequest().body(Map.of("message", "Order not found"));
			}
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

				return "admin/editBooking"; // Assuming "editBooking" is the name of your Thymeleaf template
			} else {
				// Booking not found, handle this case (e.g., show an error message)
				return "redirect:/admin/payments";
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
						return "admin/editBooking";
					}

					currentBooking.setOrigin(bookingDetails.getOrigin());
					currentBooking.setDestination(bookingDetails.getDestination());
					currentBooking.setDate(bookingDetails.getDate());
					currentBooking.setTickets(bookingDetails.getTickets());
					currentBooking.setTotalAmount(bookingDetails.getTotalAmount());

					bookingDetailsRepository.save(currentBooking);

					return "redirect:/admin/payments";
				} else {
					model.addAttribute("error", "Booking not found.");
					return "admin/editBooking";
				}
			} catch (Exception e) {
				model.addAttribute("error", "An error occurred. Please try again later.");
				return "admin/editBooking";
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
				return "redirect:/admin/payments";
			}
			return "redirect:/admin/payments";
		}

		@GetMapping("/BookingChecking/{id}")
		public String showPaymentPage(@PathVariable("id") Long bookingId, Model model) {
			BookingDetails bookingDetails = bookingDetailsRepository.findById(bookingId).orElse(null);
			if (bookingDetails != null) {
				model.addAttribute("totalAmount", bookingDetails.getTotalAmount());
				model.addAttribute("booking_id", bookingDetails.getId());

				return "admin/payment";
			} else {
				model.addAttribute("error", "Booking details not found.");
				return "admin/booking";
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

	        ByteArrayInputStream bis = PDFGenerator.bookingDetailsPDFReport(bookingDetails,findByBookingDetails_Id);

	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Disposition", "inline; filename=bookings.pdf");

	        return ResponseEntity.ok().headers(headers).contentType(org.springframework.http.MediaType.APPLICATION_PDF)
	                .body(new InputStreamResource(bis));
	    }


}
