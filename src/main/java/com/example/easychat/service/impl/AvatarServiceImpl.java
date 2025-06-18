package com.example.easychat.service.impl;

import com.example.easychat.entity.po.Avatar;
import com.example.easychat.mapper.AvatarMapper;
import com.example.easychat.service.AvatarService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AvatarServiceImpl implements AvatarService {
    @Resource
    private AvatarMapper avatarMapper;


    @Override
    public void insertAvatar(Avatar avatar) {
        this.avatarMapper.saveAvatar(avatar.getId(), avatar.getAvatarUrl());
    }

    @Override
    public String getAvatar(String id) {
        return avatarMapper.getAvatar(id);
    }
    @Override
    public void updateAvatar(Avatar avatar) {
        this.avatarMapper.updateAvatar(avatar.getId(), avatar.getAvatarUrl());
    }

}
