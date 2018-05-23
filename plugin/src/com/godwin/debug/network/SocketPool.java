package com.godwin.debug.network;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Godwin on 4/26/2018 10:15 AM for plugin.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class SocketPool {
    private static final Object MUTEX = new Object();
    private static SocketPool sSocketPool;
    private final List<ClientSocket> mClientSockets;
    private Random mRandom;

    private SocketPool() {
        //prevent object creation
        mClientSockets = new ArrayList<>();
        mRandom = new Random();
    }

    public static SocketPool getInstance() {
        if (null == sSocketPool) {
            synchronized (MUTEX) {
                sSocketPool = new SocketPool();
            }
        }
        return sSocketPool;
    }

    public void addConnection(ClientSocket clientSocket) {
        clientSocket.setuId(mRandom.nextInt(1000));
        mClientSockets.add(clientSocket);
    }

    public void removeConnection(ClientSocket clientSocket) {
        mClientSockets.remove(clientSocket);
    }

    public void removeConnection(WebSocket webSocket) {
        for (int i = 0; i < mClientSockets.size(); i++) {
            if (mClientSockets.get(i).getuId() == (Integer) webSocket.getAttachment()) {
                mClientSockets.remove(i);
                break;
            }
        }
    }

    public void clear() {
        mClientSockets.clear();
    }

    public List<ClientSocket> listConnectedSockets() {
        return mClientSockets;
    }

    public ClientSocket getClientSocket(int uId) {
        for (ClientSocket clientSocket : mClientSockets) {
            if (clientSocket.getuId() == uId) {
                return clientSocket;
            }
        }
        return null;
    }
}
