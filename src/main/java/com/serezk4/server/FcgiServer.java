package com.serezk4.server;

import com.serezk4.server.worker.TestWorker;

public class FcgiServer {
    public static void main(String[] args) {
        new Thread(new TestWorker()).start();
    }
}
