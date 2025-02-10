package com.shifter.shifter_back.annotations;

import com.shifter.shifter_back.validations.PasswordValidator;
import com.shifter.shifter_back.validations.UniqueEmailValidator;
import jakarta.validation.Constraint;

import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

;

@Constraint(validatedBy = PasswordValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default  "Password does not meet required criteria.";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
