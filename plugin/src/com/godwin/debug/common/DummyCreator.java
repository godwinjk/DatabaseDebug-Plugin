package com.godwin.debug.common;

import com.godwin.debug.model.DApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Godwin on 5/22/2018 10:45 AM for plugin.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class DummyCreator {
    public static List<DApplication> createApp() {
        List<DApplication> applicationList = new ArrayList<>();
        DApplication application = new DApplication();
        application.setVersion("1.0.1");
        application.setAppName("Dummy App");
        application.setPackageName("com.godwin.dummy");
        applicationList.add(application);
        return applicationList;
    }
}
