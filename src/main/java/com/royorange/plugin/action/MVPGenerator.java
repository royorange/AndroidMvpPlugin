package com.royorange.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.royorange.plugin.presenter.MVPClassPresenter;
import com.royorange.plugin.presenter.MVPJavaPresenter;
import com.intellij.openapi.application.Result;
import org.jetbrains.annotations.NotNull;


public class MVPGenerator extends AnAction {
    private MVPClassPresenter presenter;

    @Override
    public void actionPerformed(AnActionEvent e) {
        final PsiFile file = e.getData(LangDataKeys.PSI_FILE);
        if(file instanceof PsiJavaFile){
            presenter = new MVPJavaPresenter();
        }
        final Project project = e.getData(CommonDataKeys.PROJECT);
        new WriteCommandAction(project){
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                presenter.generate(project,file);
            }
        }.execute();
//        new WriteCommandAction<>(project,file) {
//            @Override
//            protected void run(@NotNull Result result) throws Throwable {
//                presenter.generate(project,file);
//            }
//        }.execute();

    }
}
