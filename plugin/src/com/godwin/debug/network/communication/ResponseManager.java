package com.godwin.debug.network.communication;

import org.java_websocket.WebSocket;

/**
 * Created by Godwin on 5/7/2018 2:51 PM for plugin.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public interface ResponseManager {

    void processResponse(WebSocket socket, String response);

}
