package com.filemover.thread;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public final class Worker implements Runnable {
    private final CyclicBarrier barrier;

    private final Path source;

    private final Path destination;

    public Worker(CyclicBarrier barrier, Path source, Path destination) {
        this.barrier = barrier;
        this.source = source;
        this.destination = destination;
    }

    private void moveFile(Path source, Path destination) throws IOException {
        Files.move(source, destination.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void run() {
        try {
            moveFile(source, destination);
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException | IOException e) {
            e.printStackTrace();
        }
    }
}
