package com.example.easychat.entity.enums;

public enum MessageTypeEnum {
    INIT(0, "", "连接ws获取消息"),
    ADD_FRIEND(1, "", "添加好友打招呼消息"),
    CHAT(2,"","普通聊天消息"),
    GROUP_CREATE(3,"群组已经创建成功，可以一起聊天啦！","创建群聊消息"),
    CONTACT_APPLY(4,"","好友申请"),
    MEDIA_CHAT(5,"","媒体文件"),
    FILE_UPLOAD(6,"","文件上传完成"),
    UPLOAD_USERINFO(7,"","修改个人信息"),
    ADD_GROUP(9,"%s加入了群组", "加入群聊"),
    GROUP_NAME_UPDATE(10,"","更新群昵称"),
    LEAVE_GROUP(11, "%s退出了群聊", "退出群聊"),
    REMOVE_GROUP(12,"%s被管理员移除了群聊","被管理员移除了群聊"),
    ADD_FRIEND_SELF(13,"", ""),
    JOIN_GROUP(14, "", ""),
    AUDIO_MESSAGE(15,"","语音消息");


    private Integer type;
    private String initMessage;
    private String desc;

    public Integer getType() {
        return type;
    }

    public String getInitMessage() {
        return initMessage;
    }

    public String getDesc() {
        return desc;
    }

    MessageTypeEnum(Integer type, String initMessage, String desc) {
        this.desc = desc;
        this.initMessage = initMessage;
        this.type = type;
    }

    public static MessageTypeEnum getByType(Integer type) {
        for (MessageTypeEnum item : MessageTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }

}
