package com.richardvinz.registrationwithemailverification.service;


import com.richardvinz.registrationwithemailverification.entity.User;
import com.richardvinz.registrationwithemailverification.entity.registraion.VerificationToken;
import com.richardvinz.registrationwithemailverification.model.RegistrationRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User>getUser();
    User RegisterUser(RegistrationRequest request);
    Optional<User>findByEmail(String email);

    void saveUserVerificationToken(User theUser, String verificationToken);

    String validateToken(String theToken);

    VerificationToken generateVerificationToken(String oldToken);
}
