package com.serezk4.server.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FIleLogger implements Logger{
    private final String name;
    private final Path path;
    private final BufferedWriter writer;
    private static final Path baseDir = Path.of("logs");

    public FIleLogger(String name)  {
        this.name = name;
        this.path = Path.of("logs", name + ":" + System.currentTimeMillis() + ".log");
        if (!baseDir.toFile().exists()) {
            baseDir.toFile().mkdir();
        }
        if (!path.toFile().exists()) {
            try {
                path.toFile().createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            this.writer = Files.newBufferedWriter(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void log(String message) {
        try {
            writer.append(message).append("\n").flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
