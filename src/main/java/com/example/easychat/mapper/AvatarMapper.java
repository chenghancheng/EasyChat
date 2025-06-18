package com.example.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.easychat.entity.po.Avatar;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface AvatarMapper extends BaseMapper<Avatar> {

    @Select("SELECT avatar_url FROM avatar WHERE id = #{id}")
    String getAvatar(@Param("id") String id);


    @Insert("INSERT INTO avatar (id, avatar_url) VALUES (#{id}, #{avatarUrl})")
    void saveAvatar(@Param("id") String id, @Param("avatarUrl") String avatarUrl);


    @Update("UPDATE avatar SET avatar_url = #{avatarUrl} WHERE id = #{id}")
    void updateAvatar(@Param("id") String id, @Param("avatarUrl") String avatarUrl);

}
