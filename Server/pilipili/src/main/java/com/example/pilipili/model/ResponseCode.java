package com.example.pilipili.model;

import lombok.Getter;

/**
 * ResponseCode Enum
 * Success(0, "SUCCESS")
 * FAIL(-1, "FAIL")
 */
@Getter
public enum ResponseCode {
    SUCCESS(0, "SUCCESS"),
    FAIL(-1, "FAIL");

    private int code;
    private String msg;

    /**
     *
     * @param code 0: success; -1: fail.
     * @param msg message that you want to send to client.
     */
    ResponseCode(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

}
