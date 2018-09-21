package com.alfredteng.casetrace.utils;

import java.io.IOException;

public interface BaseHttpResultListener {

    public void onRespStatus(String body);

    public void onRespMapList(String body) throws IOException;

    public void onRespError();

    public void onReqFailure(Object object);

    public void  onRespSessionExpired();

}
