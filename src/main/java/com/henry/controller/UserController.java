package com.henry.controller;

import com.henry.model.User;
import com.henry.service.UserService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/save")
    public User add(@RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping("/update/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        return userService.update(id, user);
    }

    @GetMapping("/findOne/{id}")
    public User findOne(@PathVariable Long id) {
        return userService.findOne(id);
    }

    @GetMapping("/all")
    public Iterable<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/findByName/{name}")
    public Iterable<User> findByName(@PathVariable String name) {
        return userService.findByName(name);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
         userService.delete(id);
    }

}


