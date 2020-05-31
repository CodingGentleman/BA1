package at.fhj.genb.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("javax.persistence.Entity")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class AnnotationProcessor extends AbstractProcessor {
    private static final String FACTORY_POSTFIX = "Factory";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream()
                .map(roundEnv::getElementsAnnotatedWith)
                .flatMap(Set::stream)
                .forEach(this::process);
        return false;
    }

    private void process(Element element) {
        var clazz = (TypeElement) element;
        var standardSrcCreator = new StandardSrcCreator(processingEnv, clazz);
        standardSrcCreator.createBuildInterface();
        standardSrcCreator.process();
        var fullSrcCreator = new FullSrcCreator(processingEnv, clazz);
        fullSrcCreator.process();
        createFactory(clazz, standardSrcCreator, fullSrcCreator);
    }

    private void createFactory(TypeElement clazz, SrcCreator standardSrcCreator, SrcCreator fullSrcCreator) {
        var clazzName = clazz.getSimpleName().toString();
        var prefix = "AEIOU".indexOf(clazzName.charAt(0)) > -1 ? "an" : "a";
        writeFile(
                JavaFile.builder(processingEnv.getElementUtils().getPackageOf(clazz).getQualifiedName().toString(),
                        TypeSpec.classBuilder(clazzName + FACTORY_POSTFIX)
                                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                .addMethod(createFactoryMethod(prefix + clazzName, standardSrcCreator))
                                .addMethod(createFactoryMethod("aFull" + clazzName, fullSrcCreator))
                                .build())
                        .build()
        );
    }

    private MethodSpec createFactoryMethod(String methodName, SrcCreator srcCreator) {
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement("return new " + srcCreator.getBuilderImpl().canonicalName() + "()")
                .returns(srcCreator.getStartingInterface())
                .build();
    }

    private void writeFile(JavaFile javaFile) {
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
