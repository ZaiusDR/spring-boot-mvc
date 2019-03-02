package info.esuarez.springmvcdemo.validators;

import info.esuarez.springmvcdemo.annotations.CourseCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CourseCodeConstraintValidator implements ConstraintValidator<CourseCode, String> {

   private String courseCode;

   @Override
   public void initialize(CourseCode courseCode) {
      this.courseCode = courseCode.value();
   }

   @Override
   public boolean isValid(String code, ConstraintValidatorContext context) {

      if (code != null) {
         return code.startsWith(courseCode);
      }
      else {
         return true;
      }
   }
}
