package com.royorange.plugin.presenter;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

public interface MVPClassPresenter {

    void generate(Project project,PsiFile file);

    void generateActivity(Project project,VirtualFile file);

    void generateFragment(Project project,VirtualFile file);
}
