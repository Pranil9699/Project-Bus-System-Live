/*
const paymentStart = () => {
	let amount = $("#amount").val();

	if (amount == "" || amount == null) {
		console.log("here")
		swal("Required!", "Amount is Required!!!", "warning");
		return;
	}
	console.log(`m ${amount}`)
	let amountInPaisa = Math.round(parseFloat(amount) * 100);
	$.ajax({
		url: '/user/create_order',
		data: JSON.stringify({ amount: amountInPaisa, bookingId: $("#bookingId").val() }),
		contentType: 'application/json',
		type: 'POST',
		dataType: 'json',
		success: function(res) {
			let response = res;
			console.log(response);
			if (response.status == "created") {
				let options = {
					key: 'rzp_test_QFvMW8KhGXxtl9',
					amount: amountInPaisa,
					currency: 'INR',
					name: "Lal Pari Bus",
					description: "Donation",
					image: "https://avatars.githubusercontent.com/u/99954777?v=4?s=400",
					order_id: response.id,
					handler: function(response) {
						updatePayment(response.razorpay_payment_id, response.razorpay_order_id, 'Paid', $("#bookingId").val());
					},
					prefill: { name: "takawane pranil", email: "takawanepranil22@gmail.com", contact: "9699532910" },
					notes: { address: "pargaon Daund pune" },
					theme: { color: "#3399cc" }
				};

				let rzp = new Razorpay(options);
				rzp.on('payment.failed', function(_response) {
					alert("Oops Payment Failed !!");
				});

				rzp.open();
			}
		},
		error: function(_xhr, _textStatus, errorThrown) {
			console.log("n")
			console.log(errorThrown);
			swal("Failed !", "An error occurred. Please try again later.", "error");
		}
	});
}

function updatePayment(paymentId, orderId, status, b) {
	$.ajax({
		url: "/user/update_order",
		data: JSON.stringify({ paymentId: paymentId, orderId: orderId, status: status, b: b }),
		contentType: "application/json",
		type: "POST",
		dataType: "json",
		success: function(response) {
			swal("Good Job!", response.message, "success");
		},
		error: function(_error) {
			console.log("o")
			swal("Failed !", "Your Payment is Successful, But we did not get on server, we will convey you as soon as by Razorpay.com !!", "error");
		}
	});
}

*/




const paymentStart = (type) => {
	let amount = $("#amount").val();

	if (amount == "" || amount == null) {
		console.log("here")
		swal("Required!", "Amount is Required!!!", "warning");
		return;
	}
	let amountInPaisa = Math.round(parseFloat(amount) * 100);
	$.ajax({
		url: `/${type}/create_order`,
		data: JSON.stringify({ amount: amountInPaisa, bookingId: $("#bookingId").val() }),
		contentType: 'application/json',
		type: 'POST',
		dataType: 'json',
		success: function(res) {
			let response = res;
			if (response.status == "created") {
				let options = {
					key: 'rzp_test_QFvMW8KhGXxtl9',
					amount: amountInPaisa,
					currency: 'INR',
					name: "Lal Pari Bus",
					description: "Donation",
					image: "https://avatars.githubusercontent.com/u/99954777?v=4?s=400",
					order_id: response.id,
					handler: function(response) {
						updatePayment(response.razorpay_payment_id, response.razorpay_order_id, 'Paid', $("#bookingId").val(),type);
					},
					prefill: { name: "takawane pranil", email: "takawanepranil22@gmail.com", contact: "9699532910" },
					notes: { address: "pargaon Daund pune" },
					theme: { color: "#3399cc" }
				};

				let rzp = new Razorpay(options);
				rzp.on('payment.failed', function(_response) {
					alert("Oops Payment Failed !!");
				});
				
				rzp.open();
			}
		},
		error: function(_xhr, _textStatus, errorThrown) {
			swal("Failed !", "An error occurred. Please try again later.", "error");
		}
	});
}

function updatePayment(paymentId, orderId, status, b,type) {
	$.ajax({
		url: `/${type}/update_order`,
		data: JSON.stringify({ paymentId: paymentId, orderId: orderId, status: status, b: b }),
		contentType: "application/json",
		type: "POST",
		dataType: "json",
		success: function(response) {
			swal("Good Job! GO to Payments And Get Tickets", response.message, "success");
			window.location.href = `/${type}/payments`;
		},
		error: function(_error) {
			console.log("o")
			swal("Failed !", "Your Payment is Successful, But we did not get on server, we will convey you as soon as by Razorpay.com !!", "error");
		}
	});
}
