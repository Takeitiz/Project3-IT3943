package com.project3.auth.repository;

import com.project3.auth.entity.Auth;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends MongoRepository<Auth, String> {
}
