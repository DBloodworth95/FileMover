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

    private static final int MAX_FILES = 2;

    private int numberOfFiles = 0;

    private final JLabel numberOfFilesToMoveL = new JLabel(numberOfFiles + "/5 files are ready to move.");

    private final JLabel namingConventionL = new JLabel("Your file convention:");

    private final JTextField namingConventionTF = new JTextField();

    private final JButton addFileBtn = new JButton("Select file");

    private final JButton moveFilesBtn = new JButton("Move files");

    private final JFileChooser fileChooser = new JFileChooser();

    private final List<File> fileList = new ArrayList<>(MAX_FILES);

    private final ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final CyclicBarrier barrier = new CyclicBarrier(2, this::whenDone);

    private int fileTracker = 0;

    public Display() {
        setTitle(TITLE);
        setSize(400, 600);
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
        namingConventionL.setBounds(20, 400, 150, 10);
        namingConventionTF.setBounds(200, 400, 150, 20);
        addFileBtn.addActionListener(this);
        moveFilesBtn.addActionListener(this);
        add(numberOfFilesToMoveL);
        add(addFileBtn);
        add(moveFilesBtn);
        add(namingConventionL);
        add(namingConventionTF);
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
        numberOfFilesToMoveL.setText(numberOfFiles + "/5 files are ready to move.");
        System.out.println(file.getName() + " has been added to the file list!");
    }

    //Invoked when the user chooses a new file path.
    private void handleNewFileDestination(JFileChooser fileChooser) {
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileList.size() == MAX_FILES)
            doPar();
        else
            JOptionPane.showMessageDialog(null, "Please add more files before trying to move!");
    }

    //Schedules worker threads to move the files in parallel.
    public void doPar() {
        for (File file : fileList) {
            fileTracker++;
            Path source = file.toPath();
            Path newDestination = fileChooser.getCurrentDirectory().toPath();
            String namingConvention = namingConventionTF.getText() + fileTracker;
            executors.submit(new Worker(barrier, source, newDestination, namingConvention));
            System.out.println("Sending worker out to move from: " + source + " to: " + newDestination);
        }
        fileList.clear();
    }

    //Cleans up the workers, ready for another file moving operation.
    private void whenDone() {
        System.out.println("Files moved!");
    }
}
