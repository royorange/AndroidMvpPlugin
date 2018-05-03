package com.royorange.plugin.presenter;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.InheritanceUtil;

import java.util.ArrayList;
import java.util.List;

public class MVPJavaPresenter extends PresenterImpl {
    private static final String DI_PACKAGE = "com.arvato.sephora.app.presentation.internal.di";
    private static final String BASE_PACKAGE = "com.arvato.sephora.app.presentation.view.base";

    Project project;
    PsiElementFactory elementFactory;


    @Override
    public void generate(Project project,PsiFile file) {
        this.project = project;
        elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
        generate((PsiJavaFile)file);
    }

    @Override
    public void generateActivity(Project project, VirtualFile file) {
        this.project = project;
        elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
    }

    @Override
    public void generateFragment(Project project, VirtualFile file) {
        this.project = project;
        elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
    }

    public void generate(PsiJavaFile file) {
        PsiClass psiClass = file.getClasses()[0];
//        boolean isActivity = InheritanceUtil.isInheritor(psiClass,"android.app.Activity");
        boolean isActivity = InheritanceUtil.isInheritor(psiClass,"com.roy.Activity")
                ||psiClass.getName().endsWith("Activity");
        if(isActivity){
            generateActivityProcess(file,psiClass);
        }
        boolean isFragment = InheritanceUtil.isInheritor(psiClass,"com.roy.Fragment")
                ||psiClass.getName().endsWith("Fragment");
        if(isFragment){
            generateFragmentProcess(file,psiClass);
        }
    }

    private void generateActivityProcess(PsiJavaFile file,PsiClass activityClass){
        String prefix;
        String className = activityClass.getName();
        int end = className.indexOf("Activity");
        if(end > 0){
            prefix = className.substring(0,end);
        }else {
            prefix = className;
        }

        createContract(prefix,file.getContainingDirectory());
        createPresenter(prefix,file.getContainingDirectory());
        createModule(prefix,file.getContainingDirectory());
        if(activityClass.getExtendsList().getReferencedTypes().length == 0){
            StringBuilder stringBuilder = new StringBuilder("BaseActivity<");
            stringBuilder.append(prefix).append("Contract.Presenter,DataBinding>");
            activityClass.getExtendsList().add(elementFactory.createReferenceFromText(stringBuilder.toString(),activityClass));
        }
        if(activityClass.getImplementsList().getReferencedTypes().length == 0){
            String contractName = prefix + "Contract.View";
            activityClass.getImplementsList().add(elementFactory.createReferenceFromText(contractName,activityClass));
        }
    }

    private void generateFragmentProcess(PsiJavaFile file,PsiClass fragmentClass){
        String prefix;
        String className = fragmentClass.getName();
        int end = className.indexOf("Fragment");
        if(end > 0){
            prefix = className.substring(0,end);
        }else {
            prefix = className;
        }

        createContract(prefix,file.getContainingDirectory());
        createPresenter(prefix,file.getContainingDirectory());
        if(fragmentClass.getExtendsList().getReferencedTypes().length == 0){
            StringBuilder stringBuilder = new StringBuilder("BaseFragment<");
            stringBuilder.append(prefix).append("Contract.Presenter,DataBinding>");
            fragmentClass.getExtendsList().add(elementFactory.createReferenceFromText(stringBuilder.toString(),fragmentClass));
        }
        if(fragmentClass.getImplementsList().getReferencedTypes().length == 0){
            String contractName = prefix + "Contract.View";
            fragmentClass.getImplementsList().add(elementFactory.createReferenceFromText(contractName,fragmentClass));
        }
    }

    private void createContract(String prefix,PsiDirectory directory){
        PsiClass contract = createClassFileIfNotExist(prefix,"Contract",directory);
        //add View
        addInnerInterface(contract,"View","BaseView");
        //add Presenter
        addInnerInterface(contract,"Presenter","BasePresenter","View");
    }

    private void createPresenter(String prefix,PsiDirectory directory){
        PsiClass presenter = createClassFileIfNotExist(prefix,"Presenter",directory);
        String contractView = prefix + "Contract.View";
        String contractPresenter = prefix + "Contract.Presenter";
        addImport((PsiJavaFile)presenter.getContainingFile(),BASE_PACKAGE+".RxPresenter");
        if(presenter.getExtendsList().getReferencedTypes().length == 0){
            String basePresenterName = "RxPresenter<"+ contractView + ">";
            presenter.getExtendsList().add(elementFactory.createReferenceFromText(basePresenterName,presenter));
        }
        if(presenter.getImplementsList().getReferencedTypes().length == 0){
            presenter.getImplementsList().add(elementFactory.createReferenceFromText(contractPresenter,presenter));
        }
    }

    private void createModule(String prefix,PsiDirectory directory){
        PsiClass module = createClassFileIfNotExist(prefix,"PresenterModule",directory);
        List<String> importList = new ArrayList<>();
        importList.add("dagger.Binds");
        importList.add("dagger.Module");
        importList.add(DI_PACKAGE+".scope.ActivityScoped");
        addImport((PsiJavaFile)module.getContainingFile(),importList);
        module.getModifierList().setModifierProperty(PsiModifier.ABSTRACT,true);
        if(module.getModifierList().getAnnotations().length==0){
            module.getModifierList().addAnnotation("Module");
        }
        generatePresenterInModule(prefix,module);
    }

    private void generatePresenterInModule(String prefix,PsiClass module){
        String presenterName = prefix.substring(0,1).toLowerCase()+prefix.substring(1) + "Presenter";
        //already exist
        if(module.findMethodsByName(presenterName,false).length>0){
            return;
        }
        String presenterClassName = prefix + "Presenter";
        String contractName = prefix + "Contract";
        StringBuilder stringBuilder = new StringBuilder("abstract ");
        stringBuilder.append(contractName)
                .append(".Presenter ")
                .append(presenterName)
                .append("(")
                .append(presenterClassName)
                .append(" ").append(presenterName).append(");");
        PsiMethod method = elementFactory.createMethodFromText(stringBuilder.toString(),module);
        method.getModifierList().addAnnotation("Binds");
        method.getModifierList().addAnnotation("ActivityScoped");
        module.add(method);
    }

    private void addInnerInterface(PsiClass psiClass,String className,String extendedFrom){
        addInnerInterface(psiClass,className,extendedFrom,null);
    }

    private void addInnerInterface(PsiClass psiClass,String className,String extendedFrom,String generics){
        if(psiClass.findInnerClassByName(className,true)!=null){
            return;
        }
        PsiClass anInterface = elementFactory.createInterface(className);
        String superName = extendedFrom;
        if(generics!=null&&generics.length()>0){
            superName = extendedFrom +"<" + generics + ">";
        }
        anInterface.getExtendsList().add(elementFactory.createReferenceFromText(superName,anInterface));
        //delete default added public modifier for interface
        anInterface.getModifierList().getChildren()[0].delete();
        addImport((PsiJavaFile)psiClass.getContainingFile(),BASE_PACKAGE + "." + extendedFrom);
        psiClass.add(anInterface);
    }

    private PsiFile findJavaExist(PsiDirectory directory,String name){
        return directory.findFile(name+".java");
    }

    private PsiClass createClassFileIfNotExist(String prefix,String functionName,PsiDirectory directory){
        String className = prefix + functionName;
        PsiClass classFile;
        PsiFile targetFile = findJavaExist(directory,className);
        if(targetFile!=null){
            classFile = ((PsiJavaFile)targetFile).getClasses()[0];
        }else {
            classFile = JavaDirectoryService.getInstance().createClass(directory,className);
        }
        return classFile;
    }

    private PsiElement addImport(PsiJavaFile javaFile, String fullyQualifiedName) {

        final PsiImportList importList = javaFile.getImportList();
        if (importList == null) {
            return null;
        }

        // Check if already imported
        for (PsiImportStatement is : importList.getImportStatements()) {
            String impQualifiedName = is.getQualifiedName();
            if (fullyQualifiedName.equals(impQualifiedName)) {
                return null; // Already imported so nothing needed
            }
        }
        // Not imported yet so add it
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(fullyQualifiedName,javaFile.getResolveScope());
        PsiElement element;
        if(psiClass!=null){
            element = importList.add(elementFactory.createImportStatement(psiClass));
        }else {
            element = importList.add(elementFactory.createImportStatementOnDemand(fullyQualifiedName));
        }
        return element;
    }

    private PsiElement addImport(PsiJavaFile javaFile, List<String> fullyQualifiedName) {
        final PsiImportList importList = javaFile.getImportList();
        if (importList == null) {
            return null;
        }
        PsiElement element = null;
        // Check if already imported
        for(String name:fullyQualifiedName){
            String needAdd = name;
            for (PsiImportStatement is : importList.getImportStatements()) {
                String impQualifiedName = is.getQualifiedName();
                if (name.equals(impQualifiedName)) {
                    needAdd = null;
                    break;
                }else {
                    needAdd = name;
                }
            }
            // Not imported yet so add it
            if(needAdd!=null){
                PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(needAdd,javaFile.getResolveScope());
                if(psiClass!=null){
                    element = importList.add(elementFactory.createImportStatement(psiClass));
                }else {
                    element = importList.add(elementFactory.createImportStatementOnDemand(needAdd));
                }
            }
        }
        return element;
    }
}
