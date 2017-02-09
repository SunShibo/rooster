package com.sun.rooster.util;

/**
 * Created by Shibo on 17/1/23.
 */
public class HttpRequestException extends Exception{

    public static final int HTTP_STATUS_ERRPR = 500;

    public HttpRequestException() {
    }

    public HttpRequestException(int errorCode , String errMsg , Exception e) {
    }

}
