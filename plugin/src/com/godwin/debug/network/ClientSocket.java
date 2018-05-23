package com.godwin.debug.network;

import com.godwin.debug.model.DApplication;
import com.godwin.debug.model.DDatabase;
import com.godwin.debug.model.DTable;
import com.godwin.debug.network.communication.CommunicationService;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

/**
 * Created by Godwin on 4/26/2018 10:16 AM for plugin.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ClientSocket {
    private WebSocket socket;
    private ClientHandshake handshake;
    private long connectedTime;
    private int uId;
    private DApplication application;

    /**
     * Instantiates a new Client socket.
     *
     * @param socket        the socket
     * @param connectedTime the connected time
     */
    public ClientSocket(WebSocket socket, long connectedTime) {
        this.socket = socket;
        this.connectedTime = connectedTime;
    }

    /**
     * Gets socket.
     *
     * @return the socket
     */
    public WebSocket getSocket() {
        return socket;
    }

    /**
     * Sets socket.
     *
     * @param socket the socket
     */
    public void setSocket(WebSocket socket) {
        this.socket = socket;
    }

    /**
     * Gets handshake.
     *
     * @return the handshake
     */
    public ClientHandshake getHandshake() {
        return handshake;
    }

    /**
     * Sets handshake.
     *
     * @param handshake the handshake
     */
    public void setHandshake(ClientHandshake handshake) {
        this.handshake = handshake;
    }

    /**
     * Gets connected time.
     *
     * @return the connected time
     */
    public long getConnectedTime() {
        return connectedTime;
    }

    /**
     * Sets connected time.
     *
     * @param connectedTime the connected time
     */
    public void setConnectedTime(long connectedTime) {
        this.connectedTime = connectedTime;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getuId() {
        return uId;
    }

    /**
     * Sets id.
     *
     * @param uId the u id
     */
    public void setuId(int uId) {
        this.uId = uId;
        socket.setAttachment(this.uId);
    }

    /**
     * Request app details.
     */
    public void requestAppDetails() {
        String string = CommunicationService.getRequestService().getDeviceDetails();
        socket.send(string);
    }

    /**
     * Request db details.
     */
    public void requestDbDetails() {
        String string = CommunicationService.getRequestService().getDbRequest();
        socket.send(string);
    }

    /**
     * Request table details.
     *
     * @param table
     */
    public void requestTableDetails(DTable table) {
        String string = CommunicationService.getRequestService().getTableDetailsRequest(table);
        socket.send(string);
    }

    public void executeQuery(DDatabase database, String query) {
        String string = CommunicationService.getRequestService().getExecuteQueryRequest(database, query);
        socket.send(string);
    }

    /**
     * Gets application.
     *
     * @return the application
     */
    public DApplication getApplication() {
        return application;
    }

    /**
     * Sets application.
     *
     * @param application the application
     */
    public void setApplication(DApplication application) {
        this.application = application;
    }
}
