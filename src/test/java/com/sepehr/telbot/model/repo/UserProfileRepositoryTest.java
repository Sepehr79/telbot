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
        userProfileRepository.save(new UserProfile("1", "1351", "456"));

        Optional<UserProfile> byId = userProfileRepository.findById("1");
        Assertions.assertTrue(byId.isPresent());
        Assertions.assertEquals("1351", byId.get().getChatId());
    }

}
