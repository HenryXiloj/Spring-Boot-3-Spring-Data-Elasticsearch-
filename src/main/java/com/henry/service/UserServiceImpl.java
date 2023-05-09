package com.henry.service;

import com.henry.model.User;
import com.henry.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public final class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User user) {
        var obj = userRepository.findById(id);
        obj.get().setName(user.getName());
        obj.get().setLastName(user.getLastName());
        return userRepository.save(obj.get());
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findOne(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Iterable<User> findByName(String name) {
        return userRepository.findByName(name);
    }
}
