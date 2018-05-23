package com.godwin.debugger.networking;

import com.godwin.debugger.Debugger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Godwin on 5/8/2018 6:20 PM for L_and_B.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class NetworkConnectionManager {
    private static final Object MUTEX = new Object();
    private static NetworkConnectionManager sInstance = null;
    private WebClient mClient;
    private Timer mTimer;

    public static NetworkConnectionManager getInstance() {
        if (null == sInstance) {
            synchronized (MUTEX) {
                sInstance = new NetworkConnectionManager();
            }
        }
        return sInstance;
    }

    public void connect() throws URISyntaxException {
        if (null == mClient || mClient.isClosed() || mClient.isClosing()) {
            mClient = new WebClient(Debugger.getContext(), new URI("ws://localhost:45569"));
            mClient.connect();
        }
        checkAlive();
    }

    private void checkAlive() {
        if (null == mTimer) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (!isConnected()) {
                        try {
                            connect();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            mTimer = new Timer();
            mTimer.schedule(task, 20 * 1000, 20 * 1000);
        }
    }

    private boolean isConnected() {
        return mClient != null && mClient.isOpen();
    }

    public void disConnect() {
        if (mClient != null) {
            mClient.close();
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
