package info.esuarez.springmvcdemo.annotations;

import info.esuarez.springmvcdemo.validators.CourseCodeConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CourseCodeConstraintValidator.class)
@Target( {ElementType.METHOD, ElementType.FIELD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface CourseCode {

    public String value() default "ZDR";

    public String message() default "must start with ZDR";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
