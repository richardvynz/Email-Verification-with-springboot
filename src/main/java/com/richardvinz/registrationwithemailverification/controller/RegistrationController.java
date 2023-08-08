package com.richardvinz.registrationwithemailverification.controller;

import com.richardvinz.registrationwithemailverification.entity.User;
import com.richardvinz.registrationwithemailverification.entity.registraion.VerificationToken;
import com.richardvinz.registrationwithemailverification.model.event.RegistrationCompleteEvent;
import com.richardvinz.registrationwithemailverification.model.RegistrationRequest;
import com.richardvinz.registrationwithemailverification.model.event.listener.ApplicationCompleteEventListener;
import com.richardvinz.registrationwithemailverification.repository.VerificationTokenRepository;
import com.richardvinz.registrationwithemailverification.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RequestMapping("/register")
@RestController
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ApplicationCompleteEventListener eventListener;
    private final HttpServletRequest servletRequest;

    @PostMapping
    public String registerUser(@RequestBody RegistrationRequest registrationRequest,
                               final HttpServletRequest request){
        User user = userService.RegisterUser(registrationRequest);
//        Publish registration event
    publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "Success!, please check your email to complete the registration";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token){

        String url = applicationUrl(servletRequest)+"/register/resend-verification-token?token="+ token;

        VerificationToken theToken = verificationTokenRepository.findByToken(token);

         if(theToken.getUser().getIsEnabled()){
            return "this account is already verified!, please login";
        }
        String verificationResult = userService.validateToken(token);
        if(verificationResult.equalsIgnoreCase("valid")){
            return "Email verified successfully, now you can login to your account";
        }
        return "invalid verification Token, <a href=\""+url+"\">Get a new verification link";
    }

    @GetMapping("resend-verification-token")
    public String resendVerificationToken(@RequestParam("token") String oldToken, final HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        VerificationToken verificationToken = userService.generateVerificationToken(oldToken);
        User theUser = verificationToken.getUser();
        resendVerificationTokenEmail(theUser,applicationUrl(request),verificationToken);
        return "A new verification link has been sent to your email." +
                " lease, check to activate your registration.";
    }

    private void resendVerificationTokenEmail(User theUser,
                                              String applicationUrl,
                                              VerificationToken verificationToken)
            throws MessagingException, UnsupportedEncodingException {

        String url = applicationUrl +"/register/verifyEmail?token="+verificationToken;
        eventListener.sendVerificationEmail(url);
        log.info("Click the link to verify your registration: {}", url);
    }


    public String applicationUrl(HttpServletRequest request){
        return  "http://" +request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }


}
