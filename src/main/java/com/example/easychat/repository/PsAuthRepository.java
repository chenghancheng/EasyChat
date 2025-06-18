package com.example.easychat.repository;

import com.example.easychat.entity.po.PsAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PsAuthRepository extends JpaRepository<PsAuth, String> {
}
