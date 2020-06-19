package com.filemover.thread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileMoverThread extends Thread {

    private final File file;

    private final Path source;

    private final Path destination;

    public FileMoverThread(File file, Path source, Path destination) {
        this.file = file;
        this.source = source;
        this.destination = destination;
    }

    @Override
    public void run() {
        super.run();
        try {
            moveFile(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveFile(Path source, Path destination) throws IOException {
        Files.move(source, destination.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
    }
}
