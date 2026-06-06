package com.swoovo.users.service;

import com.swoovo.users.dto.UserRequest;
import com.swoovo.users.dto.UserResponse;
import com.swoovo.users.entity.UserEntity;
import com.swoovo.users.exception.FileStorageException;
import com.swoovo.users.mapper.UserMapper;
import com.swoovo.users.repository.UserEntityRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserEntityRepository userEntityRepository;
    private final UserMapper userMapper;
    private final MinioService minioService;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "user", allEntries = true)
    })
    public UserResponse createUser(UserRequest userRequest) throws IOException, FileStorageException,
            EntityExistsException {
        if (userEntityRepository.findByNameAndSurname(userRequest.name(),
                userRequest.surname()).isPresent()
                || userEntityRepository.findByEmail(userRequest.email())
                .isPresent() || userEntityRepository
                .findByPhoneNumber(userRequest.phoneNumber()).isPresent()) {
            throw new EntityExistsException("User with this data already exists: " + userRequest);
        }

        minioService.uploadFile(userRequest.avatar().getName(),
                userRequest.avatar().getInputStream(),
                userRequest.avatar().getSize(),
                userRequest.avatar().getContentType());

        return getUserResponse(userEntityRepository
                .save(userMapper.fromRequest(userRequest)));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "user")
    public UserResponse findUserById(long id) throws FileStorageException {
        return getUserResponse(findUserEntityById(id));
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    @Cacheable(value = "users")
    public Page<UserResponse> findAllUsers(Pageable pageable) {
        return userEntityRepository.findAll(pageable).map(this::getUserResponse);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "user", key = "#id")
    })
    public UserResponse updateUser(long id, UserRequest userRequest) throws FileStorageException {
        UserEntity userEntity = findUserEntityById(id);

        userMapper.updateUserFromRequest(userRequest, userEntity);

        return getUserResponse(userEntityRepository.save(userEntity));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "user", key = "#id")
    })
    public void deleteUserById(long id) throws EntityNotFoundException {
        if (!userEntityRepository.existsById(id)) {
            throw new EntityNotFoundException(getUserNotFoundByIdException(id));
        }

        userEntityRepository.deleteById(id);
    }

    private UserResponse getUserResponse(UserEntity userEntity) throws FileStorageException {
        UserResponse userResponse = userMapper.toResponse(userEntity);

        userResponse.setAvatarUrl(minioService.downloadFile(userEntity.getAvatarFilePath()));

        return userResponse;
    }

    private UserEntity findUserEntityById(long id) throws EntityNotFoundException {
        return userEntityRepository.findById(id)
                .orElseThrow(() -> getUserNotFoundByIdException(id));
    }

    private EntityNotFoundException getUserNotFoundByIdException(long id) {
        return new EntityNotFoundException("User with id " + id + " not found");
    }
}
