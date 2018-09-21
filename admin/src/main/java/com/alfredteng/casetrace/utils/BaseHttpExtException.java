package com.alfredteng.casetrace.utils;

public class BaseHttpExtException extends Exception {

    private static final long serialVersionUID = 1L;

    private int msgCode;

    private Object msg;

    public BaseHttpExtException(int msgCode, Object msg){
        this.msg = msg;
        this.msgCode = msgCode;
    }

    public int getMsgCode(){
        return msgCode;
    }

    public Object getMsg(){
        return msg;
    }

}
