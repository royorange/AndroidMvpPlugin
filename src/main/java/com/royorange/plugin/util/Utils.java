package com.royorange.plugin.util;

import com.intellij.psi.*;

/**
 * Created by Roy on 17/5/27.
 */
public class Utils {

    public static PsiClass getClass(PsiFile file){
        PsiClass currentClass = null;
        if(file instanceof PsiJavaFile){
            currentClass = ((PsiJavaFile) file).getClasses()[0];
        }
        return currentClass;
    }

    public static boolean checkMethodExist(PsiClass psiClass, String methodName){
        return checkMethodExist(psiClass,methodName,null);
    }


    public static boolean checkMethodExist(PsiClass psiClass, String methodName, String... arguments){
        boolean exist = false;
        PsiMethod[] methods = psiClass.findMethodsByName(methodName, false);
        for (PsiMethod method : methods) {
            if(arguments == null){
                exist = true;
                break;
            }
            PsiParameterList parameterList = method.getParameterList();

            if (parameterList.getParametersCount() == arguments.length) {
                boolean isEquat = true;
                PsiParameter[] parameters = parameterList.getParameters();
                for (int i = 0; i < arguments.length; i++) {
                    if (!parameters[i].getType().getCanonicalText().equals(arguments[i])) {
                        isEquat = false;
                    }
                }

                if (isEquat) {
                    exist = true;
                }
            }
        }
        return exist;
    }
}
