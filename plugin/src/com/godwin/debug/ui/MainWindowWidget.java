package com.godwin.debug.ui;

import com.godwin.debug.model.DApplication;
import com.godwin.debug.model.DDatabase;
import com.godwin.debug.network.ClientSocket;
import com.godwin.debug.network.SocketPool;
import com.godwin.debug.network.communication.DataCommunicationListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Godwin on 4/24/2018 6:20 PM for DatabaseDebug.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class MainWindowWidget {
    private JPanel container;
    private JList deviceList;

    private Project mProject;
    private Disposable parent;

    private boolean isRunning = true;
    private List<ClientSocket> sockets = new ArrayList<>();

    private SessionWindow mSessionWindow;
    private ClientSocket mSelectSocket;

    DataCommunicationListener listener = new DataCommunicationListener() {
        @Override
        public void onGetDbData(List<DDatabase> databaseList) {

        }

        @Override
        public void onGetTableDetails(List<List<String>> table, List<String> header) {

        }

        @Override
        public void onCloseClient(ClientSocket socket) {
            if (mSelectSocket != null && socket != null) {
                if (mSelectSocket.getuId() == socket.getuId()) {
                    if (mSessionWindow != null) {
                        mSessionWindow.close();
                    }
                }
            }
        }
    };

    public MainWindowWidget(Project mProject, Disposable parent) {
        this.mProject = mProject;
        this.parent = parent;

        runDeviceDetection();
        beautify();
        deviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        deviceList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;

            JList theList = (JList) e.getSource();
            int index = theList.getSelectedIndex();
            if (sockets != null && sockets.size() > index && index > -1) {
                if (mSelectSocket != null && mSelectSocket.getuId() == sockets.get(index).getuId())
                    return;

                mSelectSocket = sockets.get(index);
                mSessionWindow = new SessionWindow(mProject, parent, mSelectSocket);
                mSessionWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        mSessionWindow = null;
                        mSelectSocket = null;
                    }
                });
            }
        });
    }

    private void beautify() {
        deviceList.setCellRenderer(new CellRenderer());
    }

    private void runDeviceDetection() {
        Thread thread = new Thread(() -> {
            while (isRunning) {
                try {

                    List<ClientSocket> clientSockets = SocketPool.getInstance().listConnectedSockets();

                    setItemsToList(clientSockets);
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void setItemsToList(List<ClientSocket> clientSockets) {
        sockets.clear();
        List<DApplication> applications = new ArrayList<>();
        for (ClientSocket socket : clientSockets) {
            if (socket.getApplication() != null) {
                applications.add(socket.getApplication());
                sockets.add(socket);
            }
        }
        deviceList.setListData(applications.toArray());
    }

    public JPanel getContainer() {
        return this.container;
    }

}
