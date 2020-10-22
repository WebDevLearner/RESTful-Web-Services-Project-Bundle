package com.behl.app.ws.shared;

import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.behl.app.ws.shared.dto.UserDto;

@Service
public class AmazonSES { 

// This class must be verified with SES

	// This address must be verified with Amazon SES
	final String FROM = "behlnavneet@gmail.com";

	// The subject line for the email
	final String SUBJECT = "Final step to complete your registration with PhotoApp";
	
	final String PASSWORD_RESET_SUBJECT = "Password Reset Request";

	// The HTML body for the email
	final String HTMLBODY = "<h1> Please verify your email address </h1>" + "</p> click the following link</p>"
			+ "<a href='http://localhost:8080/verification-service/email-verification.html?token=$tokenValue'"
			+ "Final Step to complete your registration" + "</a><br/><br/>"
			+ "Thank you! And we are waiting for you inside!";

	// The email body for non-HTML clients
	final String TEXTBODY = "please verify your email adress." + "open the following URL"
			+ "http://localhost:8080/verification-service/email-verification.html?token=$tokenValue"
			+ "Thank you! And we are waiting for you inside!";
	
	// The HTML body for the email	
	final String PASSWORD_RESET_HTMLBODY = "<h1>Password Reset Request</h1>"
			+ "<p>Hi, $firstName!</p>"
			+ "<p>Someone requested a password reset. Please ignore if that was not you otherwise click on link below: </p>"
			+ "<a href='http://localhost:8080/verification-service/password-reset.html?token=$tokenValue'>"
			+ "Click this link to reset Password"
			+ "</a></br></br>"
			+ "Thank you!";	
	
	// The HTML body for the email
	final String PASSWORD_RESET_TEXTBODY = "A reqeust to reset your password"
			+ "Hi, $firstName! "
			+ "Someone requested a password reset. Please ignore if that was not you otherwise open the link below:"
			+ "http://localhost:8080/verification-service/password-reset.html?token=$tokenValue"
			+ "Thank you!!!";
	

//	AWSCredentialsProvider provider = new ClasspathPropertiesFileCredentialsProvider();
//	AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withCredentials(provider)
//			.build();

	public void verifyEmail(UserDto userDto) {
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_1)
				.build();

		String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", userDto.getEmailVerificationToken());
		String textBodyWithToken = TEXTBODY.replace("$tokenVAlue", userDto.getEmailVerificationToken());

		SendEmailRequest request = new SendEmailRequest()
				.withDestination(new Destination().withToAddresses(userDto.getEmail()))
				.withMessage(new Message()
						.withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
								.withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
						.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
				.withSource(FROM);

		client.sendEmail(request);

		System.out.println("Email sent!!!");

	}
	
	
	public boolean sendPasswordResetRequest(String firstName, String email, String token) {
		boolean returnValue = false;
		
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
		
		String htmlBodyWithToken = PASSWORD_RESET_HTMLBODY.replace("$tokenValue", token);
		htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstName);
		
		String textBodyWithToken = PASSWORD_RESET_TEXTBODY.replace("$tokenValue", token);
		textBodyWithToken = textBodyWithToken.replace("$firstName", firstName);
		
		SendEmailRequest request = new SendEmailRequest()
				.withDestination(new Destination().withToAddresses(email))
				.withMessage(new Message()
						.withBody(new Body()
						.withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
						.withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
						.withSubject(new Content().withCharset("UTF-8").withData(PASSWORD_RESET_SUBJECT)))
						.withSource(FROM);

		SendEmailResult result = client.sendEmail(request);
		
		if(result != null && (result.getMessageId()!=null && !result.getMessageId().isEmpty())){
			returnValue = true;
		}
		
		return returnValue;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
