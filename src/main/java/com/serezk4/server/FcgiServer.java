package com.serezk4.server;

import com.serezk4.server.worker.TestWorker;

public class FcgiServer {
    public static void main(String[] args) {
        System.out.println("loaded");
        new Thread(new TestWorker()).start();
    }
}
