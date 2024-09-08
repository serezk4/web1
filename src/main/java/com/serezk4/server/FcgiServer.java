package com.serezk4.server;

import com.serezk4.server.worker.CoordinatesValidationWorker;

public class FcgiServer {
    public static void main(String[] args) {
        new Thread(new CoordinatesValidationWorker()).start();
    }
}
