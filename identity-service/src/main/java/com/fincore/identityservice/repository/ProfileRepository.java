package com.fincore.identityservice.repository;

import com.fincore.identityservice.entity.Profile;
import com.fincore.identityservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    boolean existsByEmailAndUserNot(String email, User user);

    boolean existsByPhoneNumberAndUserNot(String phoneNumber, User user);

}
