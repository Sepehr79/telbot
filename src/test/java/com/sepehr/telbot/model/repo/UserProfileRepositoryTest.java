package com.sepehr.telbot.model.repo;

import com.sepehr.telbot.model.entity.UserProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class UserProfileRepositoryTest {

    @Autowired
    UserProfileRepository userProfileRepository;

    @Test
    @Disabled
    void saveAndReadTest() {

    }

}
