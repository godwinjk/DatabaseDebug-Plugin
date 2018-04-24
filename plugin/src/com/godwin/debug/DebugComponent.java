package com.godwin.debug;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

/**
 * Created by Godwin on 4/22/2018 9:30 AM for DatabaseDebug.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class DebugComponent extends AbstractProjectComponent {
    protected DebugComponent(Project project) {
        super(project);
    }
}
