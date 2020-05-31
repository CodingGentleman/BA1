package at.fhj.genb.processor;

import com.squareup.javapoet.ClassName;

public interface SrcCreator {
    void process();
    ClassName getStartingInterface();
    ClassName getBuilderImpl();
}
