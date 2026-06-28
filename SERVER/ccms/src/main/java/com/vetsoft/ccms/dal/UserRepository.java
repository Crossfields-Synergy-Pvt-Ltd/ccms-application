package com.vetsoft.ccms.dal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.vetsoft.ccms.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}
