package com.richardvinz.registrationwithemailverification.service.serviceImp;

import com.richardvinz.registrationwithemailverification.repository.UserRepository;
import com.richardvinz.registrationwithemailverification.security.UserRegistrationDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationDetailsService  implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(UserRegistrationDetails::new)
                .orElseThrow(()->new UsernameNotFoundException("user with the given email doesn't exist"));
    }
}
