package at.fhj.genb.processor;

import at.fhj.genb.service.GenbPersistenceProvider;
import com.squareup.javapoet.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.persistence.*;
import javax.tools.Diagnostic;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

abstract class BaseSrcCreator implements SrcCreator {
    protected static final String BUILDER_POSTFIX = "Builder";
    protected static final String FIELD_NAME = "entity";

    private final ProcessingEnvironment processingEnv;
    private final Types types;
    private final TypeElement srcClazz;
    private final PackageElement srcPackageElement;
    private final String builderPackageName;
    private ClassName startingInterface;

    private final List<Element> allFields = new ArrayList<>();
    private final List<Element> mandatoryFields = new ArrayList<>();
    private final List<Element> optionalFields = new ArrayList<>();

    protected BaseSrcCreator(ProcessingEnvironment processingEnvironment, TypeElement srcClazz) {
        this.processingEnv = processingEnvironment;
        this.types = processingEnvironment.getTypeUtils();
        this.srcClazz = srcClazz;
        this.srcPackageElement = processingEnvironment.getElementUtils().getPackageOf(srcClazz);
        this.builderPackageName = srcPackageElement.getQualifiedName().toString()+".genb."+subPackageName();
    }

    protected TypeElement getSrcClazz() {
        return srcClazz;
    }

    protected String getBuilderInterfacesPackageName() {
        return builderPackageName;
    }

    @Override
    public ClassName getBuilderImpl() {
        return ClassName.get(srcPackageElement.getQualifiedName().toString(), srcClazz.getSimpleName() + capitaliseFirstLetter(subPackageName()) + BUILDER_POSTFIX);
    }

    @Override
    public ClassName getStartingInterface() {
        return startingInterface;
    }

    protected List<Element> getAllFields() {
        return allFields;
    }

    protected List<Element> getMandatoryFields() {
        return mandatoryFields;
    }

    protected List<Element> getOptionalFields() {
        return optionalFields;
    }

    @Override
    public void process() {
        findFields();
        var builderImpl = startBuilderImpl();
        startingInterface = createSourceFiles(builderImpl);
        writeFile(JavaFile.builder(getBuilderImpl().packageName(), builderImpl.build()).build());
    }

    protected abstract String subPackageName();
    protected abstract ClassName createSourceFiles(TypeSpec.Builder builderImpl);

    private void findFields() {
        var clazzElements = new ArrayList<Element>(srcClazz.getEnclosedElements());
        var parent = types.asElement(srcClazz.getSuperclass());
        addParentElementsRecursively(clazzElements, parent);
        clazzElements.stream()
                .filter(e -> e.getKind().isField())
                .filter(e -> !e.getModifiers().contains(Modifier.FINAL))
                .filter(e -> e.getAnnotation(Transient.class) == null)
                .filter(e -> e.getAnnotation(GeneratedValue.class) == null)
                .sorted(Comparator.comparing(o -> o.getSimpleName().toString()))
                .forEachOrdered(element -> {
                    if(element.getAnnotation(NotNull.class) != null || element.getAnnotation(Id.class) != null) {
                        mandatoryFields.add(element);
                    } else {
                        optionalFields.add(element);
                    }
                });
        allFields.addAll(mandatoryFields);
        allFields.addAll(optionalFields);
    }

    private void addParentElementsRecursively(List<Element> elements, Element element) {
        if (element instanceof TypeElement && (element.getAnnotation(MappedSuperclass.class) != null || element.getAnnotation(Entity.class) != null)) {
            elements.addAll(element.getEnclosedElements());
            TypeMirror parent = ((TypeElement) element).getSuperclass();
            addParentElementsRecursively(elements, types.asElement(parent));
        }
    }

    public void createBuildInterface() {
        writeFile(JavaFile.builder(getBuildInterfaceName().packageName(),
                TypeSpec.interfaceBuilder(getBuildInterfaceName().simpleName())
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(MethodSpec.methodBuilder("build")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(getSourceTypeName()).build()).build()).build());
    }

    protected TypeSpec.Builder startBuilderImpl() {
        return TypeSpec.classBuilder(getBuilderImpl())
                .addModifiers(Modifier.FINAL)
                .addSuperinterface(getBuildInterfaceName())
                .addField(getSourceTypeName(), FIELD_NAME, Modifier.PRIVATE)
                .addMethod(MethodSpec.constructorBuilder()
                        .addStatement(FIELD_NAME + " = new " + srcClazz.getSimpleName() + "()")
                        .build())
                .addMethod(MethodSpec.methodBuilder("build")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(getSourceTypeName())
                        .addStatement(GenbPersistenceProvider.class.getCanonicalName()+".getInstance().loadService().persist("+FIELD_NAME+")")
                        .addStatement("return " + FIELD_NAME).build());
    }

    protected ClassName createFieldInterfaceImpl(TypeSpec.Builder builderImpl, ClassName builderImplReturnTypeName, Element field, ClassName returnType) {
        var currentBuilderImplReturnTypeName = builderImplReturnTypeName;
        var fieldTypeName = TypeName.get(field.asType());
        var capitalFieldName = capitaliseFirstLetter(field);
        var interfaceName = ClassName.get(getBuilderInterfacesPackageName(), getSrcClazz().getSimpleName() + capitalFieldName);
        if (currentBuilderImplReturnTypeName == null) {
            currentBuilderImplReturnTypeName = interfaceName;
        }
        builderImpl.addSuperinterface(interfaceName);
        writeFile(JavaFile.builder(getBuilderInterfacesPackageName(),
                TypeSpec.interfaceBuilder(interfaceName)
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(MethodSpec.methodBuilder("with" + capitalFieldName)
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .addParameter(fieldTypeName, "value")
                                .returns(returnType).build())
                        .build()).build());

        builderImpl.addMethod(MethodSpec.methodBuilder("with" + capitalFieldName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldTypeName, "value")
                .returns(returnType)
                .addStatement(FIELD_NAME + ".set" + capitaliseFirstLetter(field) + "(value)")
                .addStatement("return this").build());
        return currentBuilderImplReturnTypeName;
    }

    protected ClassName getBuildInterfaceName() {
        return ClassName.get(srcPackageElement.getQualifiedName().toString()+".genb", srcClazz.getSimpleName() + "Build");
    }

    protected ClassName getSourceTypeName() {
        return ClassName.get(srcPackageElement.getQualifiedName().toString(), srcClazz.getSimpleName().toString());
    }

    protected String capitaliseFirstLetter(Element field) {
        return capitaliseFirstLetter(field.getSimpleName().toString());
    }

    protected String capitaliseFirstLetter(String value) {
        return (value.substring(0, 1).toUpperCase() + value.substring(1));
    }

    protected void writeFile(JavaFile javaFile) {
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            err(e);
            throw new IllegalStateException(e);
        }
    }

    private void err(Exception e) {
        var stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, stringWriter.toString());
    }
}
