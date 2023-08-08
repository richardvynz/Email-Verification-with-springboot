package com.richardvinz.registrationwithemailverification.service.serviceImp;

import com.richardvinz.registrationwithemailverification.entity.User;
import com.richardvinz.registrationwithemailverification.entity.registraion.VerificationToken;
import com.richardvinz.registrationwithemailverification.exception.UserAlreadyExistException;
import com.richardvinz.registrationwithemailverification.model.RegistrationRequest;
import com.richardvinz.registrationwithemailverification.repository.UserRepository;
import com.richardvinz.registrationwithemailverification.repository.VerificationTokenRepository;
import com.richardvinz.registrationwithemailverification.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationRepository;
    @Override
    public List<User> getUser() {
        return userRepository.findAll();
    }

    @Override
    public User RegisterUser(RegistrationRequest request) {

        Optional<User> user = this.findByEmail(request.email());
        if(user.isPresent()){
            throw new UserAlreadyExistException("user with :"+request.email()+"already exist");
        }
        var newUser = new User();
        newUser.setFirstName(request.firstName());
        newUser.setLastName(request.lastName());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(request.role());

        return userRepository.save(newUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(User theUser, String token) {
        var verificationToken = new VerificationToken(token,theUser);
        verificationRepository.save(verificationToken);
    }

    @Override
    public String validateToken(String theToken) {
        VerificationToken token = verificationRepository.findByToken(theToken);

        if(token==null){
            return "return invalid verification token";
        }
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if((token.getTokenExpirationTime().getTime() - calendar.getTime().getTime())<=0){
            verificationRepository.delete(token);
            return "token already expired";
        }
        user.setIsEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public VerificationToken generateVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationRepository.findByToken(oldToken);
        var verificationTokenTime = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenTime.setExpirationTime(verificationTokenTime.getTokenExpirationTime());
        verificationRepository.save(verificationToken);
        return null;
    }
}
