package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        AdminUserDTO user = (AdminUserDTO) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }
}
