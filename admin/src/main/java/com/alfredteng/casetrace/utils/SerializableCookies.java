package com.alfredteng.casetrace.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import okhttp3.Cookie;

public class SerializableCookies implements Serializable{

    private transient final Cookie cookies;
    private transient Cookie clientCookies;

    public SerializableCookies (Cookie cookies) {
        this.cookies = cookies;
    }

    public Cookie getCookies() {
        Cookie cookie = cookies;
        if (clientCookies != null) {
            cookie = clientCookies;
        }
        return cookie;
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeObject(cookies.name());
        outputStream.writeObject(cookies.value());
        outputStream.writeLong(cookies.expiresAt());
        outputStream.writeObject(cookies.domain());
        outputStream.writeObject(cookies.path());
        outputStream.writeBoolean(cookies.secure());
        outputStream.writeBoolean(cookies.httpOnly());
        outputStream.writeBoolean(cookies.hostOnly());
        outputStream.writeBoolean(cookies.persistent());
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        String name = (String)inputStream.readObject();
        String value = (String)inputStream.readObject();
        long expiresAt = inputStream.readLong();
        String domain = (String) inputStream.readObject();
        String path = (String) inputStream.readObject();
        boolean secure = inputStream.readBoolean();
        boolean httpOnly = inputStream.readBoolean();
        boolean hostOnly = inputStream.readBoolean();
        boolean persistent = inputStream.readBoolean();
        Cookie.Builder builder = new Cookie.Builder();
        builder = builder.name(name);
        builder = builder.value(value);
        builder = builder.expiresAt(expiresAt);
        builder = hostOnly ? builder.hostOnlyDomain(domain) : builder.domain(domain);
        builder = builder.path(path);
        builder = secure ? builder.secure() : builder;
        builder = httpOnly ? builder.httpOnly() : builder;
        clientCookies =builder.build();
    }

}
