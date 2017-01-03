package org.demosoft.cloudhub.infra;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Conditional(WindowsDetector.class)
public @interface Windows {
    boolean value();
}