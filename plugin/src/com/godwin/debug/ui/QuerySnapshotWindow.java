package com.godwin.debug.ui;

import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Created by Godwin on 5/23/2018 2:03 PM for plugin.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class QuerySnapshotWindow {
    private JPanel container;
    private JBTable mTable;
    private JFrame mFrame;

    private List<String> header;
    private List<List<String>> data;

    public QuerySnapshotWindow(List<List<String>> data, List<String> header) {
        super();

        this.header = header;
        this.data = data;

        mFrame = new JFrame();

        mFrame.setSize(new Dimension(1024, 500));
        mFrame.setResizable(true);
        mFrame.setTitle("Debug window");
        mFrame.setLayout(new BorderLayout());
        mFrame.add(container, BorderLayout.CENTER);
        mFrame.pack();
        mFrame.setVisible(true);

        populateTable();
    }

    private void populateTable() {
        mTable = new JBTable();
        container.removeAll();

        container.add(mTable, BorderLayout.CENTER);
        container.repaint();
        container.revalidate();

        String[][] array = new String[data.size()][];
        for (int i = 0; i < data.size(); i++) {
            List<String> row = data.get(i);
            try {
                array[i] = row.toArray(new String[row.size()]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DefaultTableModel model = new DefaultTableModel(array, header.toArray());

        mTable.setModel(model);
    }
}
