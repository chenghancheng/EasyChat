package com.example.easychat.entity.po;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Data
@Entity
@Table(name = "ps_aors")
public class PsAor {
    @Id
    private String id;
    @Column(name = "max_contacts")
    private Integer maxContacts;
}