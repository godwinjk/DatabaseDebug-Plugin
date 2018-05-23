package com.godwin.debug.network.communication;

import com.godwin.debug.model.DDatabase;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by Godwin on 5/7/2018 7:58 PM for plugin.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class DataObserver {
    private static final Object MUTEX = new Object();
    private static DataObserver sObserver;

    private List<DataCommunicationListener> mListeners;

    private DataObserver() {
        //prevent object creation
        mListeners = new ArrayList<>();
    }

    public static DataObserver getInstance() {
        if (null == sObserver) {
            synchronized (MUTEX) {
                if (null == sObserver) {
                    sObserver = new DataObserver();
                }
            }
        }
        return sObserver;
    }

    public void subscribe(DataCommunicationListener listener) {
        try {
            if (null == listener)
                throw new NullPointerException();

            if (!mListeners.contains(listener)) {
                mListeners.add(listener);
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    public void unSubcribe(DataCommunicationListener listener) {
        try {
            mListeners.remove(listener);
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    public void publish(List<DDatabase> databases) {
        for (int i = 0; i < mListeners.size(); i++) {
            DataCommunicationListener listener = mListeners.get(i);
            listener.onGetDbData(databases);
        }
    }

    public void publishTable(List<List<String>> table, List<String> header) {
        for (int i = 0; i < mListeners.size(); i++) {
            DataCommunicationListener listener = mListeners.get(i);
            listener.onGetTableDetails(table, header);
        }
    }
}
