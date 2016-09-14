package org.osgl.util;

import java.lang.annotation.Annotation;

public interface AnnotationAware {

    public <T extends Annotation> T getAnnotation(Class<T> annoClass);

    public boolean hasAnnotation(Class<? extends Annotation> annoClass);

}
