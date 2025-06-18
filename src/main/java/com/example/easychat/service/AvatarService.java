package com.example.easychat.service;

import com.example.easychat.entity.po.Avatar;

public interface AvatarService {
    void insertAvatar(Avatar avatar);

    String getAvatar(String id);

    void updateAvatar(Avatar avatar);
}
