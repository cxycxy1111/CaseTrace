package com.example.alfredtools;

import java.io.IOException;

public interface HttpResultListener {

    public void onRespStatus(String body,int source);

    public void onRespMapList(String body,int source) throws IOException;

    public void onRespError(int source);

    public void onReqFailure(Object object,int source);

    public void  onRespSessionExpired(int source);
}
