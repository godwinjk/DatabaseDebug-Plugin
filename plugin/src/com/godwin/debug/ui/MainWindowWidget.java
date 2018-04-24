package com.godwin.debug.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * Created by Godwin on 4/24/2018 6:20 PM for DatabaseDebug.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class MainWindowWidget {
    private JPanel container;
    private JButton submit;
    private JTextField iptextField;
    private JPanel innerContainer;

    private Project mProject;
    private Disposable parent;

    public MainWindowWidget(Project mProject, Disposable parent) {
        this.mProject = mProject;
        this.parent = parent;


    }
}
