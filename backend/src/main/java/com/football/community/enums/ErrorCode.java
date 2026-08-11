package com.football.community.enums;

import lombok.Getter;

/**
 * 业务错误码枚举。
 */
@Getter
public enum ErrorCode {

    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户名已存在"),
    PASSWORD_INCORRECT(1003, "密码错误"),
    TOKEN_INVALID(1004, "Token无效或已过期"),
    TOKEN_REVOKED(1005, "会话已失效"),

    PRODUCT_NOT_FOUND(2001, "商品不存在"),
    STOCK_INSUFFICIENT(2002, "库存不足"),
    ORDER_NOT_FOUND(2003, "订单不存在"),
    ORDER_STATUS_INVALID(2004, "订单状态异常"),

    MATCH_NOT_FOUND(3001, "比赛不存在"),
    TEAM_REGISTERED(3002, "已报名该比赛"),

    COMMENT_NOT_FOUND(4001, "评论不存在"),
    COMMENT_ALREADY_LIKED(4002, "已点赞过该评论");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
