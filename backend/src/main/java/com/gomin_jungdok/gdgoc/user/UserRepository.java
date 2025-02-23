package com.gomin_jungdok.gdgoc.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gomin_jungdok.gdgoc.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}

