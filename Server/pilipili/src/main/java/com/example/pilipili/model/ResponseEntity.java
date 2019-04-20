package com.example.pilipili.model;

/** Response Model which will be sent to side. */
public class ResponseEntity {

    private int code;
    private String message;
    private Object data;

    /**
     * create a ResponseEntity
     * @param responseCode 0: success; -1: fail.
     * @param message message that you want to send to client.
     */
    public ResponseEntity(ResponseCode responseCode, String message){
        this.code = responseCode.getCode();
        this.message = message;
    }

    /**
     * create a ResponseEntity
     * @param responseCode 0: success; -1: fail.
     * @param message message that you want to send to client.
     * @param data data part of the response. e.g. User user
     */
    public ResponseEntity(ResponseCode responseCode, String message, Object data){
        this(responseCode, message);
        this.data = data;
    }

    /**
     * Get Response Code
     * @return 0: success; -1: fail.
     */
    public int getCode() {
        return code;
    }

    /**
     * Get Response Message
     * @return message that you want to send to client.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get Response data
     * @return response data Object e.g. User
     */
    public Object getData() {
        return data;
    }

}
