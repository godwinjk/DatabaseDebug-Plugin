package com.godwin.debug.network;

import com.godwin.debug.adb.AdbCommandExecutor;
import com.godwin.debug.common.Logger;
import com.godwin.debug.network.communication.CommunicationService;
import com.godwin.debug.network.communication.DataCommunicationListener;
import com.godwin.debug.network.communication.ResponseManager;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * Created by Godwin on 4/25/2018 12:20 PM for plugin.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class NetworkConnectionManager extends WebSocketServer {
    private static final Object MUTEX = new Object();
    private static NetworkConnectionManager sManager;
    private final String TAG = getClass().getSimpleName();
    private AdbCommandExecutor mExecutor;
    private boolean isRunning = true;

    private DataCommunicationListener mListener;

    private NetworkConnectionManager() {
        super(new InetSocketAddress("localhost", PortAllocationManager.getInstance().getPort(0)));
        //prevent object creation
        mExecutor = new AdbCommandExecutor();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static NetworkConnectionManager getInstance() {
        if (null == sManager) {
            synchronized (MUTEX) {
                sManager = new NetworkConnectionManager();
            }
        }
        return sManager;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        addToRegistry(webSocket, clientHandshake);

        Logger.d(TAG, "OnOpen : " + webSocket.getResourceDescriptor()
                + " address: " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        removeFromRegistry(webSocket);

        Logger.d(TAG, "onClose : " + webSocket.getResourceDescriptor()
                + " address: " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());

        Logger.d(TAG, "\ni : " + i
                + "\n String: " + s
                + "\n boolean: " + b);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

        ResponseManager manager = CommunicationService.getResponseService();
        manager.processResponse(webSocket, s);
        Logger.d(TAG, "onMessage : " + webSocket.getResourceDescriptor()
                + " address: " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());

        Logger.i(TAG, "Message: " + s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        removeFromRegistry(webSocket);
        webSocket.close();
        Logger.d(TAG, "onError : " + webSocket.getResourceDescriptor()
                + " address: " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
        e.printStackTrace();
    }

    @Override
    public void onStart() {
        isRunning = true;
        Logger.d(TAG, "Server started successfully ");
    }

    /**
     * Start server.
     */
    public void startServer() {
        setReuseAddr(true);
        start();
    }

    private void addToRegistry(WebSocket socket, ClientHandshake handshake) {
        ClientSocket clientSocket = new ClientSocket(socket, System.currentTimeMillis());
        clientSocket.setHandshake(handshake);
        SocketPool.getInstance().addConnection(clientSocket);

        clientSocket.requestAppDetails();
    }

    private void removeFromRegistry(WebSocket socket) {
        SocketPool.getInstance().removeConnection(socket);
    }

    private void removeAllSockets() {
        SocketPool.getInstance().clear();
    }

    /**
     * Is running boolean.
     *
     * @return the boolean
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Sets running.
     *
     * @param running the running
     */
    public void setRunning(boolean running) {
        isRunning = running;
    }

    public DataCommunicationListener getListener() {
        return mListener;
    }

    public void setListener(DataCommunicationListener mListener) {
        this.mListener = mListener;
    }
}
