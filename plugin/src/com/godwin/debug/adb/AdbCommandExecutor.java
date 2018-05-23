package com.godwin.debug.adb;

import com.godwin.debug.common.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Godwin on 4/26/2018 3:04 PM for plugin.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class AdbCommandExecutor {

    private final String TAG = getClass().getSimpleName();

    public String adbPortForward(int machinePort, int devicePort) {
        StringBuilder builder = new StringBuilder();
        builder.append("forward")
                .append(" ")
                .append("tcp:")
                .append(devicePort)
                .append("tcp:")
                .append(machinePort);

        return executeCommandUsingRuntime(builder.toString());
    }

    public String adbPortReverse(int machinePort, int devicePort) {
        StringBuilder builder = new StringBuilder();
        builder.append("reverse")
                .append(" ")
                .append("tcp:")
                .append(devicePort)
                .append(" ")
                .append("tcp:")
                .append(machinePort);

        return executeCommandUsingRuntime(builder.toString());
    }

    public String adbDevices() {
        StringBuilder builder = new StringBuilder();
        builder.append("devices");

        return executeCommandUsingRuntime(builder.toString());
    }

    public String adbDevicesWithAllValue() {
        StringBuilder builder = new StringBuilder();
        builder.append("devices -l");

        return executeCommandUsingRuntime(builder.toString());
    }

    private String executeCommandUsingRuntime(String subCommand) {
        Logger.d(TAG, "SubCommand : " + subCommand);
        StringBuilder builder = new StringBuilder();
        builder.append("adb").append(" ").append(subCommand);
        Logger.d(TAG, "Main Command : " + builder.toString());

        StringBuilder opBuilder = new StringBuilder();
        Runtime run = Runtime.getRuntime();
        Process pr = null;
        try {
            pr = run.exec(builder.toString());
            pr.waitFor();

            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "null";
            while ((line = buf.readLine()) != null) {
                opBuilder.append(line);
                opBuilder.append("\n");
                Logger.d(TAG, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return opBuilder.toString();
    }

    private String executeCommandUsingProcessBuilder(String subCommand) {
        Logger.d(TAG, "SubCommand : " + subCommand);
        StringBuilder builder = new StringBuilder();
        builder.append("adb").append(" ").append(subCommand);

        StringBuilder opBuilder = new StringBuilder();
        String[] splitCommands = builder.toString().split(" ");
        ProcessBuilder pb = new ProcessBuilder(splitCommands);
        Process pr = null;
        try {
            pr = pb.start();
            pr.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "null";
            while ((line = buf.readLine()) != null) {
                opBuilder.append(line);
                Logger.d(TAG, line);
            }
            Logger.d(TAG, "Done");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return opBuilder.toString();
    }
}
