package at.fhj.genb.processor;

import com.squareup.javapoet.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

class FullSrcCreator extends BaseSrcCreator {
    protected FullSrcCreator(ProcessingEnvironment processingEnvironment, TypeElement srcClazz) {
        super(processingEnvironment, srcClazz);
    }

    @Override
    protected String subPackageName() {
        return "full";
    }

    @Override
    protected ClassName createSourceFiles(TypeSpec.Builder builderImpl) {
        ClassName builderImplReturnTypeName = null;
        for (int i = 0; i < getAllFields().size(); i++) {
            var field = getAllFields().get(i);
            var returnType = getBuildInterfaceName();
            if ((i + 1) < getAllFields().size()) {
                returnType = ClassName.get(getBuilderInterfacesPackageName(),
                        getSrcClazz().getSimpleName().toString() + capitaliseFirstLetter(getAllFields().get(i + 1)));
            }
            builderImplReturnTypeName = createFieldInterfaceImpl(builderImpl, builderImplReturnTypeName, field, returnType);
        }
        return builderImplReturnTypeName;
    }
}
