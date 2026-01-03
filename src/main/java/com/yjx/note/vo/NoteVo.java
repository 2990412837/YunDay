package com.yjx.note.vo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 云记分组统计结果：按日期/类型分组
 */
@Getter
@Setter
public class NoteVo implements Serializable {//实现Serializable
    private static final long serialVersionUID = 1L;
    private String groupName; // 分组名称（日期/类型名）
    private long noteCount; // 云记数量
    private Integer typeId; // 类型ID（仅类型分组用）
}