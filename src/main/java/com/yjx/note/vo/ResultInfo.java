package com.yjx.note.vo;
import lombok.Getter;
import lombok.Setter;
/**
 * 统一返回结果封装：code=1成功，code=0失败
 */
@Getter
@Setter
public class ResultInfo<T> {
    private Integer code; // 状态码
    private String msg; // 提示信息
    private T result; // 返回数据
}