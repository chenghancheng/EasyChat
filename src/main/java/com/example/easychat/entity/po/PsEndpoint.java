package com.example.easychat.entity.po;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Data
@Entity
@Table(name = "ps_endpoints")
public class PsEndpoint {
    @Id
    private String id;
    private String transport;
    private String aors;
    private String auth;
    private String context;
    private String disallow;
    private String allow;
    @Column(name = "direct_media")
    private String directMedia;
}
