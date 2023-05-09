package com.henry.repository;

import com.henry.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Long> {

    Iterable<User> findByName(String name);
}
