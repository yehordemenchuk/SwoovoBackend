package com.swoovo.users.controller;

import com.swoovo.users.dto.UserRequest;
import com.swoovo.users.dto.UserResponse;
import com.swoovo.users.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/users/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest userRequest)
            throws Exception {
        UserResponse userResponse = userService.createUser(userRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(userResponse);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable long id)
            throws Exception {
        return ResponseEntity.ok(userService.findUserById(id));
    }


    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<UserResponse>>> getAllUsers(Pageable pageable,
                                                                             PagedResourcesAssembler<UserResponse> pagedResourcesAssembler) {

        Page<UserResponse> users = userService.findAllUsers(pageable);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(users));
    }

    @PutMapping("{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable long id,
                                                   @Valid @RequestBody UserRequest userRequest) throws Exception {
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable long id) {
        userService.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }
}
