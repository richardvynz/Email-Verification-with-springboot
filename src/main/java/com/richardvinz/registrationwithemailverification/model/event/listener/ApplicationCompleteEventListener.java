package com.richardvinz.registrationwithemailverification.model.event.listener;

import com.richardvinz.registrationwithemailverification.entity.User;
import com.richardvinz.registrationwithemailverification.model.event.RegistrationCompleteEvent;
import com.richardvinz.registrationwithemailverification.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationCompleteEventListener
        implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserService userService;
    private final JavaMailSender mailSender;
    private User theUser;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
//        GET THE NEWLY REGISTERED USER
        theUser = event.getUser();
//        CREATE A VERIFICATION TOKEN FOR THE USER
        String verificationToken = UUID.randomUUID().toString();
//        SAVE THE VERIFICATION TOKEN FOR THE USER
       userService.saveUserVerificationToken(theUser,verificationToken);
//        BUILD THE VERIFICATION URL TO BE SENT TO THE USER
        String url = event.getApplicationUrl()+"/register/verifyEmail?token="+verificationToken;
//        SEND THE EMAIL
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        log.info("Click the link to verify your registration: {}", url);
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "User Registration Portal Service";
        String mailContent ="<p> Hi, "+ theUser.getFirstName()+ ",</p>"+
                "<p> Thank you for registering with us,"+" "+
                "Please follow the link to complete your registration.</p>"+
               "<a href=\""+url+"\"> Verify your email to activate your account</a>" +
                "<p> thank you! <br> User Registration Portal Service";

        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("springemailaccout@gmail.com",senderName);
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }

}
