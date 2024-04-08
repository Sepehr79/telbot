package com.sepehr.telbot.model.repo;

import com.sepehr.telbot.model.entity.UserProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile, String> {
}
