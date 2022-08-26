package com.henry.resource;

import com.henry.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user-resource")
public class UserResource {

    private final  SearchQueryBuilder searchQueryBuilder;

    public UserResource(SearchQueryBuilder searchQueryBuilder) {
        this.searchQueryBuilder = searchQueryBuilder;
    }

    @GetMapping("/{name}")
    public List<User> getByName(@PathVariable String name) throws IOException {
       return searchQueryBuilder.getByName(name);
    }
}
