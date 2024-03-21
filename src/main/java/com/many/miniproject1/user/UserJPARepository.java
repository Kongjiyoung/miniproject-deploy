package com.many.miniproject1.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserJPARepository extends JpaRepository<User, Integer> {

    User findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
}