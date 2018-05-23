package com.godwin.debug.adb;

import com.godwin.debug.model.DDevice;
import com.godwin.debug.network.PortAllocationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Godwin on 4/26/2018 6:23 PM for plugin.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class DeviceDetectionService implements Runnable {

    private static final Object MUTEX = new Object();
    private static DeviceDetectionService mService;
    private AdbCommandExecutor executor;
    private boolean isDaemonRunning = true;

    private List<DDevice> discoveredDevices;

    private DeviceDetectionService() {
        //prevent object creation
        executor = new AdbCommandExecutor();
        discoveredDevices = new ArrayList<>();
    }

    public static DeviceDetectionService getInstance() {
        if (null == mService) {
            synchronized (MUTEX) {
                mService = new DeviceDetectionService();
            }
        }
        return mService;
    }

    public void startDetecting() {
        Thread thread = new Thread(this);
        thread.start();

    }

    @Override
    public void run() {
        while (isDaemonRunning()) {
            String deviceList = executor.adbDevicesWithAllValue();
            List<DDevice> devices = AdbDeviceSupport.getDeviceList(deviceList);
            if (devices != null && devices.size() > 0) {
                int allocated = PortAllocationManager.getInstance().getAllocatedPort();
                executor.adbPortReverse(allocated, allocated);
                break;
            }
            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isDaemonRunning() {
        return isDaemonRunning;
    }

    public void setDaemonRunning(boolean daemonRunning) {
        isDaemonRunning = daemonRunning;
    }
}
