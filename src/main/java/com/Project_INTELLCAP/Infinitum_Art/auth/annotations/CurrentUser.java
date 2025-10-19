package com.Project_INTELLCAP.Infinitum_Art.auth.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // Only works on method parameters
@Retention(RetentionPolicy.RUNTIME) // Available during runtime
public @interface CurrentUser {}
