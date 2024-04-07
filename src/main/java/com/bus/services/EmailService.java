package com.bus.services;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {

	public static boolean sendEmail(String useremail,int OTP) {
		boolean isSent=false;
		String from="takawanepranil22@gmail.com";
		// SMTP properties
		Properties smtpProperties = new Properties();
		smtpProperties.put("mail.smtp.auth", true);
		smtpProperties.put("mail.smtp.starttls.enable", true);
		smtpProperties.put("mail.smtp.ssl.enable", true);
		smtpProperties.put("mail.smtp.port", 465);
		smtpProperties.put("mail.smtp.host", "smtp.gmail.com");

		final String username = "takawanepranil22@gmail.com";
		final String password = "qonifztsuzyswhsb";
//		final String username = "lalparibuses@gmail.com";
//		final String password = "dycv yuxx ahyo ldcu";
		// lalparibuses@gmail.com
		// dycv yuxx ahyo ldcu

		Session session = Session.getInstance(smtpProperties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(useremail));
			message.setFrom(new InternetAddress(from));
			message.setSubject("Reset Password ");
			message.setText("Your OTP Is :"+OTP);
			

			Transport.send(message);
System.out.println("Email done");
			isSent = true;
		} catch (MessagingException e) {
			System.err.println("Error occurred while sending the email: " + e.getMessage());
		}

		return isSent;
	}

}
