package com.filemover.ui;

import com.filemover.thread.Worker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * A simple program that allows the user to select many files.
 * The user can select a new destination folder for these files.
 * The program will then assign a separate thread to each file moving task.
 * The threads will be executed in parallel in an attempt to maximize performance.
 */
public class Display extends JFrame implements ActionListener {

    private static final String TITLE = "Dan's File Mover";

    private int numberOfFiles = 0;

    private final JLabel numberOfFilesToMoveL = new JLabel(numberOfFiles + "/5 files are ready to move.");

    private final JButton addFileBtn = new JButton("Select file");

    private final JButton moveFilesBtn = new JButton("Move files");

    private final JFileChooser fileChooser = new JFileChooser();

    private final List<File> fileList = new ArrayList<>();

    private final ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final CyclicBarrier barrier = new CyclicBarrier(4, this::whenDone);

    public Display() {
        setTitle(TITLE);
        setSize(400, 400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(null);
        setupComponents();
    }

    //Sets up the components of the JFrame
    private void setupComponents() {
        numberOfFilesToMoveL.setBounds(100, 50, 200, 20);
        addFileBtn.setBounds(10, 200, 130, 30);
        moveFilesBtn.setBounds(250, 200, 130, 30);
        addFileBtn.addActionListener(this);
        moveFilesBtn.addActionListener(this);
        add(numberOfFilesToMoveL);
        add(addFileBtn);
        add(moveFilesBtn);
    }

    //Event handler for the buttons.
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == addFileBtn) {
            int fileChooserOption = fileChooser.showOpenDialog(Display.this);

            if (fileChooserOption == JFileChooser.APPROVE_OPTION)
                handleFileSelection(fileChooser);
        } else if (actionEvent.getSource() == moveFilesBtn) {
            int fileChooserOption = fileChooser.showOpenDialog(Display.this);

            if (fileChooserOption == JFileChooser.APPROVE_OPTION)
                handleNewFileDestination(fileChooser);
        }
    }

    //Invoked when the user clicks "Select file".
    private void handleFileSelection(JFileChooser fileChooser) {
        File file = fileChooser.getSelectedFile();
        fileList.add(file);
        ++numberOfFiles;
        numberOfFilesToMoveL.setText("Number of files ready to move: " + numberOfFiles);
        System.out.println(file.getName() + " has been added to the file list!");
    }

    //Invoked when the user chooses a new file path.
    private void handleNewFileDestination(JFileChooser fileChooser) {
        fileChooser.setAcceptAllFileFilterUsed(false);
        doPar();
    }

    //Schedules worker threads to move the files in parallel.
    public void doPar() {
        for (File file : fileList) {
            Path source = file.toPath();
            Path newDestination = fileChooser.getCurrentDirectory().toPath();
            executors.submit(new Worker(barrier, source, newDestination));
            System.out.println("Sending worker out to move from: " + source + " to: " + newDestination);
        }
        fileList.clear();
    }

    //Cleans up the workers, ready for another file moving operation.
    private void whenDone() {
        System.out.println("Files moved!");
    }
}
