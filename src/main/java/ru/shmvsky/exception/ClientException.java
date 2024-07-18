package ru.shmvsky.exception;

public class ClientException extends RuntimeException {

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
