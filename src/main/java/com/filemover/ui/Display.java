package com.filemover.ui;

import com.filemover.thread.FileMoverThread;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Display extends JFrame implements ActionListener {

    private static final String TITLE = "Dan's File Mover";

    private int numberOfFiles = 0;

    private final JLabel numberOfFilesToMoveL = new JLabel(numberOfFiles + "/5 files are ready to move.");

    private final JButton addFileBtn = new JButton("Select file");

    private final JButton moveFilesBtn = new JButton("Move files");

    private final JFileChooser fileChooser = new JFileChooser();

    private final List<File> fileList = new ArrayList<>();

    private final List<FileMoverThread> threads = new ArrayList<>();

    public Display() {
        setTitle(TITLE);
        setSize(400,400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(null);
        setupComponents();
    }

    private void setupComponents() {
        numberOfFilesToMoveL.setBounds(100,50, 200,20);
        addFileBtn.setBounds(10,200, 130,30);
        moveFilesBtn.setBounds(250,200, 130,30);
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
        }
        else if (actionEvent.getSource() == moveFilesBtn) {
            int fileChooserOption = fileChooser.showOpenDialog(Display.this);

            if (fileChooserOption == JFileChooser.APPROVE_OPTION) {
                try {
                    handleNewFileDestination(fileChooser);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleFileSelection(JFileChooser fileChooser) {
        File file = fileChooser.getSelectedFile();
        fileList.add(file);
        ++numberOfFiles;
        numberOfFilesToMoveL.setText("Number of files ready to move: " + numberOfFiles);
        System.out.println(file.getName() + " has been added to the file list!");
    }

    private void handleNewFileDestination(JFileChooser fileChooser) throws IOException {
        fileChooser.setAcceptAllFileFilterUsed(false);
        for (File file : fileList) {
            Path source = file.toPath();
            Path newDestination = fileChooser.getCurrentDirectory().toPath();
            Files.move(source, newDestination.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            threads.add(new FileMoverThread(file, source, newDestination));
        }
        System.out.println("Files loaded into threads!");

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Files moved!");
    }
}
