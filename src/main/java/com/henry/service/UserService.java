package com.henry.service;

import com.henry.model.User;

public sealed interface UserService permits UserServiceImpl {

    User save(User user);

    User update(Long id, User user);

    void delete(Long id);

    User findOne(Long id);

    Iterable<User> findAll();

    Iterable<User> findByName(String name);
    
}
