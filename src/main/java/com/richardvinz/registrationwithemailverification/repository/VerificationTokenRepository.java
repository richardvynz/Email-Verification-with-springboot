package com.richardvinz.registrationwithemailverification.repository;

import com.richardvinz.registrationwithemailverification.entity.registraion.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    VerificationToken findByToken(String token);
}
