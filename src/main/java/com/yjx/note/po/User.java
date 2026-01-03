package com.yjx.note.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer userId; // 主键，自增
    private String uname; // 用户名
    private String upwd; // 密码（MD5加密）
    private String nick; // 昵称
    private String head; // 头像路径
    private String mood; // 个性签名
}