package com.prenticehall.moss.sched;

public class IllegalConfigFileException extends Exception {
    public IllegalConfigFileException(Throwable cause) {
        super(cause);
    }

    public IllegalConfigFileException(String message) {
        super(message);
    }
}
