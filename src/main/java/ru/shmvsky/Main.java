package ru.shmvsky;

public class Main {
    public static void main(String[] args) {
        var server = new TcpEchoServer(5454);
        server.run();
    }
}