package com.filemover.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Display extends JFrame implements ActionListener {

    private static final String TITLE = "Dan's File Mover";

    private int numberOfFiles = 0;

    private final JLabel numberOfFilesToMoveL = new JLabel(numberOfFiles + "/5 files are ready to move.");

    private final JButton addFileBtn = new JButton("Select file");

    private final JButton moveFilesBtn = new JButton("Move files");

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
        add(numberOfFilesToMoveL);
        add(addFileBtn);
        add(moveFilesBtn);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }
}
