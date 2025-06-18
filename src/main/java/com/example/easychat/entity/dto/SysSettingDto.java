package com.example.easychat.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SysSettingDto implements Serializable {
    private static final long serialVersionUID = -4052701002357141142L;

    private Integer maxGroupCount = 5;
    private Integer maxGroupMemberCount = 500;

}
