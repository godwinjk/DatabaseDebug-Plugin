package com.godwin.debug.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ErrorAlertDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel jLabel;

    public ErrorAlertDialog(String message) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        jLabel.setText(message);
        buttonOK.addActionListener(e -> onOK());
    }

    private void onOK() {
        // add your code here

        dispose();
    }
}
