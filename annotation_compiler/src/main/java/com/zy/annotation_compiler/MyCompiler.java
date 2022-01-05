package com.zy.annotation_compiler;

import com.google.auto.service.AutoService;
import com.zy.annotations.MyAnnotation;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class MyCompiler extends AbstractProcessor {

    //1.支持的版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    //2.能用来处理哪些注解
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(MyAnnotation.class.getCanonicalName());
//        types.add(Override.class.getCanonicalName());
        return types;
    }

    //3.定义一个用来生成APT目录下面的文件的对象
    Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"zy==========================="+processingEnv.getSourceVersion().name());

//        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(MyAnnotation.class);
//        elementsAnnotatedWith.forEach(element -> {
//
//        });

        //获取APP中所有用到了BindView注解的对象
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(MyAnnotation.class);

//        TypeElement//类
//        ExecutableElement//方法
//          VariableElement//属性
        //开始对elementsAnnotatedWith进行分类
        Map<String, List<VariableElement>> map = new HashMap<>();
        for (Element element : elementsAnnotatedWith) {
            VariableElement variableElement = (VariableElement) element;
            String activityName = variableElement.getEnclosingElement().getSimpleName().toString();
            Class aClass = variableElement.getEnclosingElement().getClass();
            List<VariableElement> variableElements = map.get(activityName);
            if (variableElements == null) {
                variableElements = new ArrayList<>();
                map.put(activityName, variableElements);
            }
            variableElements.add(variableElement);
        }



        if (map.size() > 0) {
            Writer writer = null;
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String activityName = iterator.next();
                List<VariableElement> variableElements = map.get(activityName);
                //得到包名
                TypeElement enclosingElement = (TypeElement) variableElements.get(0).getEnclosingElement();
                String packageName = processingEnv.getElementUtils().getPackageOf(enclosingElement).toString();
                try {
                    JavaFileObject sourceFile = filer.createSourceFile(packageName + "." + activityName + "_ViewBinding");
                    writer = sourceFile.openWriter();
                    //        package com.example.dn_butterknife;
                    writer.write("package " + packageName + ";\n");
                    //        import com.example.dn_butterknife.IBinder;
                    writer.write("import " + packageName + ".IBinder;\n");
                    //        public class MainActivity_ViewBinding implements IBinder<
                    //        com.example.dn_butterknife.MainActivity>{
                    writer.write("public class " + activityName + "_ViewBinding implements IBinder<" +
                            packageName + "." + activityName + ">{\n");
                    //            public void bind(com.example.dn_butterknife.MainActivity target) {
                    writer.write(" @Override\n" +
                            " public void bind(" + packageName + "." + activityName + " target){");
                    //target.tvText=(android.widget.TextView)target.findViewById(2131165325);
                    for (VariableElement variableElement : variableElements) {
                        //得到名字
                        String variableName = variableElement.getSimpleName().toString();
                        //得到ID
                        int id = variableElement.getAnnotation(MyAnnotation.class).value();
                        String name = variableElement.getAnnotation(MyAnnotation.class).name();
                        //得到类型
                        TypeMirror typeMirror = variableElement.asType();
                        writer.write("target." + variableName + "=(" + typeMirror + ")target.findViewById(" + id + ");\n");
                        writer.write("//targetname=(" + typeMirror + ")target(" + name + ");\n");
                    }

                    writer.write("\n}}");

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }
}