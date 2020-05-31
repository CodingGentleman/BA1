package at.fhj.genb.processor;

import com.squareup.javapoet.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;


class StandardSrcCreator extends BaseSrcCreator {
    protected StandardSrcCreator(ProcessingEnvironment processingEnvironment, TypeElement srcClazz) {
        super(processingEnvironment, srcClazz);
    }

    @Override
    protected String subPackageName() {
        return "standard";
    }

    @Override
    protected ClassName createSourceFiles(TypeSpec.Builder builderImpl) {
        if(!getOptionalFields().isEmpty()) {
            buildOptionalFields(builderImpl);
        } else {
            builderImpl.addSuperinterface(getBuildInterfaceName());
        }
        ClassName builderImplReturnTypeName = null;
        for (int i = 0; i < getMandatoryFields().size(); i++) {
            var field = getMandatoryFields().get(i);

            var returnType = getOptionalFields().isEmpty() ? getBuildInterfaceName() : getOptionalsInterfaceName();
            if ((i + 1) < getMandatoryFields().size()) {
                returnType = ClassName.get(getBuilderInterfacesPackageName(),
                        getSrcClazz().getSimpleName().toString() + capitaliseFirstLetter(getMandatoryFields().get(i + 1)));
            }
            builderImplReturnTypeName = createFieldInterfaceImpl(builderImpl, builderImplReturnTypeName, field, returnType);
        }
        return builderImplReturnTypeName == null ? getOptionalsInterfaceName() : builderImplReturnTypeName;
    }

    private void buildOptionalFields(TypeSpec.Builder builderImpl) {
        builderImpl.addSuperinterface(getOptionalsInterfaceName());
        TypeSpec.Builder optionalsBuilder = TypeSpec.interfaceBuilder(getOptionalsInterfaceName().simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(getBuildInterfaceName());
        for (Element field : getOptionalFields()) {
            TypeName fieldTypeName = TypeName.get(field.asType());
            String capitalFieldName = capitaliseFirstLetter(field);

            optionalsBuilder.addMethod(MethodSpec.methodBuilder("with" + capitalFieldName)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(fieldTypeName, "value")
                    .returns(getOptionalsInterfaceName()).build());
            builderImpl.addMethod(MethodSpec.methodBuilder("with" + capitalFieldName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(fieldTypeName, "value")
                    .returns(getOptionalsInterfaceName())
                    .addStatement(FIELD_NAME + ".set" + capitaliseFirstLetter(field) + "(value)")
                    .addStatement("return this").build());
        }
        writeFile(JavaFile.builder(getBuilderInterfacesPackageName(),optionalsBuilder.build()).build());
    }

    private ClassName getOptionalsInterfaceName() {
        return ClassName.get(getBuilderInterfacesPackageName(), getSrcClazz().getSimpleName() + "Optionals");
    }
}
