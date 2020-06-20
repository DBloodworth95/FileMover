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

    public void doPar(Path source, Path newDestination) {
        executors.submit(new Worker(barrier, source, newDestination));
    }

    private void whenDone() {

    }

    private void handleFileSelection(JFileChooser fileChooser) {
        File file = fileChooser.getSelectedFile();
        fileList.add(file);
        ++numberOfFiles;
        numberOfFilesToMoveL.setText("Number of files ready to move: " + numberOfFiles);
        System.out.println(file.getName() + " has been added to the file list!");
    }

    private void handleNewFileDestination(JFileChooser fileChooser) {
        fileChooser.setAcceptAllFileFilterUsed(false);
        for (File file : fileList) {
            Path source = file.toPath();
            Path newDestination = fileChooser.getCurrentDirectory().toPath();
            doPar(source, newDestination);
        }
        System.out.println("Files moved!");
    }
}
