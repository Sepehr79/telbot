package com.sepehr.telbot.model.repo;

import com.sepehr.telbot.model.entity.ActiveChat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActiveChatRepository extends CrudRepository<ActiveChat, String> {

    List<ActiveChat> findAll();

}
