package com.royorange.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.royorange.plugin.ui.GeneratingMVPActivity;

public class MVPActivityGenerate extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        final Project project = e.getData(CommonDataKeys.PROJECT);
//        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        VirtualFile file = e.getDataContext().getData(PlatformDataKeys.VIRTUAL_FILE);
        if(file.isDirectory()){
        }
        GeneratingMVPActivity dialog = new GeneratingMVPActivity();
        dialog.pack();
        dialog.setVisible(true);
    }
}
