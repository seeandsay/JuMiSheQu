package com.drama.dto;

import com.drama.entity.User;
import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private UserInfo userInfo;

    @Data
    public static class UserInfo {
        private Long id;
        private String phone;
        private String email;
        private String nickname;
        private String avatar;

        public static UserInfo from(User user) {
            UserInfo info = new UserInfo();
            info.setId(user.getId());
            info.setPhone(user.getPhone());
            info.setEmail(user.getEmail());
            info.setNickname(user.getNickname());
            info.setAvatar(user.getAvatar());
            return info;
        }
    }
}
