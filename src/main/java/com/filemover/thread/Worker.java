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

    private final String convention;

    public Worker(CyclicBarrier barrier, Path source, Path destination, String convention) {
        this.barrier = barrier;
        this.source = source;
        this.destination = destination;
        this.convention = convention;
    }

    //Moves a file from the source to the destination.
    private void moveFile() throws IOException {
        Files.move(source, destination.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
    }

    //Copies the files and renames the copies to the new naming convention.
    private void renameFile() throws IOException {
        Files.copy(destination.resolve(source.getFileName()), destination.resolve(convention), StandardCopyOption.REPLACE_EXISTING);
    }

    //Invoked when submitted to the ExecutorService, waits at the barrier until all processes are finished.
    @Override
    public void run() {
        try {
            moveFile();
            System.out.println(Thread.currentThread().getName() + " is waiting at barrier");
            barrier.await();
            System.out.println(Thread.currentThread().getName() + " has passed barrier");
            renameFile();
        } catch (InterruptedException | BrokenBarrierException | IOException e) {
            e.printStackTrace();
        }
    }
}
