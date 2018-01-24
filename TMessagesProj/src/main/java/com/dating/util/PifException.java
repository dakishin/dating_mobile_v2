package com.dating.util;


/**
 * Created by dakishin@gmail.com
 * 17.08.2014.
 */
public class PifException extends Exception {
    public ErrorCode error;

    public PifException() {

    }

    public PifException(String detailMessage) {
        super(detailMessage);
    }

    public PifException(ErrorCode error, String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        this.error = error;
    }

    public PifException(Throwable throwable) {
        super(throwable);
    }

    public PifException(ErrorCode error) {
        super(error.name());
        this.error = error;

    }

    public PifException(ErrorCode error, Throwable e) {
        super(e.getMessage(), e);
        this.error = error;


    }
}
