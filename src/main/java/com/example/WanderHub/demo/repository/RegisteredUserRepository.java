package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.RegisteredUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface RegisteredUserRepository extends MongoRepository<RegisteredUser, String> {
    @Query("{ 'username': ?0 }")
    Optional<RegisteredUser> findByUsername(String username);

    boolean existsByUsername(String username);
    void deleteByUsername(String Username);




}
