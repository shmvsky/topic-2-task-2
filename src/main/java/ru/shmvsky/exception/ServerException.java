package ru.shmvsky.exception;

public class ServerException extends RuntimeException {

    public ServerException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
