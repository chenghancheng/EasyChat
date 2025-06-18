package com.example.easychat.entity.po;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Data
@Entity
@Table(name = "ps_auths")
public class PsAuth {
    @Id
    private String id;
    @Column(name = "auth_type")
    private String authType;
    private String password;
    private String username;
}