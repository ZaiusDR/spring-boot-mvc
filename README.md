# Spring Boot MVC Notes

Some notes taken during the tutorial

## Basic Architecture
```
_______________
|              |
|              |---------->   Front Controller 
|              |                    |
|              |                    |  Model
|     Web      |                    |
|    Browser   |
|              |               Controller      
|              |
|              |                    |
|              |                    |  Model
|              |                    |
|              |
|              |<------------  View Template
|______________|
```

* Front Controller - Is part of Spring Framework, already implemented out of the box. 
Delegates requests to some other objects in our system.
* To be implemented:
    * Models - Containers for data
    * View Templates - JSP, HTML, Thymeleaf, etc...
    * Controller - Business Logic
    
## Components

### Controller

- Handle requests.
- Retrieve/Store data (DB, WebService, etc...)
- Place data in models.
- Send it to appropriate view template.

### Model

- Just contains any type of data for shipping it between system's components.

### View Template

- Used for rendering web content. Spring is flexible and works well with many formats.


## MVC Basics

### @Controller annotation

Inherits from @Component. Supported for @ComponentScan annotation. 
This annotation allows @GetMapping (also @PostMapping, etc...) to be defined inside the class.

```
@Controller
public class AppController {
}
```

### @GetMapping annotation

When using @GetMapping("/endpoint"), GET requests to that endpoint will be processed by the
annotated method. It should return a String with the name of the view template.

```
@Controller
public class AppController {

    @GetMapping("/home")
    public String showHome() {
        ...
        ...
        return "home_template";
    }
}
```

### Encapsulating data with Models

It's possible to retrieve form data or parameters from the requests and place them into a model.
Also you can add any kind of mixed objects. Then just reference the attributes by the tag in the Template:

```
@Controller
public class AppController {

    @GetMapping("/home")
    public String showHome(HttpServletRequest request, Model model) {
        String studentName = request.getParameter("studentName");
        studentName = studentName.toUpperCase;
        
        model.addAttribute("name", studentName);
        
        return "home_template";
    }
}
```

```
<p th:text="'Student Name: ' + ${name}"></p>
```

### @RequestParam Annotation

Instead of using `HttpServletRequest` object to fetch request parameters, Spring provides an annotation for that:

```
@Controller
public class AppController {

    @GetMapping("/home")
    public String showHome(@RequestParam("studentName") String studentName, Model model) {
            
        studentName = studentName.toUpperCase;
        
        model.addAttribute("name", studentName);
        
        return "home_template";
    }
}
```

### @RequestMapping Annotation

Used for grouping mappings:

```
@Controller
@RequestMapping("/api")
public class AppController {

    @GetMapping("/objects")
    public String showHome(
            @RequestParam("studentName") String studentName, 
            Model model) {
            
        studentName = studentName.toUpperCase;
        
        model.addAttribute("name", studentName);
        
        return "home_template";
    }
}
```

This maps requests with format `/api/objects`.


## MVC Forms 

### Thymeleaf Data Binding

Spring and Thymeleaf allow data binding between the Getter/Setter methods of an object for each field.
It's only necessary to expose the object inside the model to the "GET" view template:

`Student` Class:

```
public class Student {

    private String firstName;
    private String lastName;

    public Student() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
```

`StudentController`:

```
@Controller
@RequestMapping("/student")
public class StudentController {

    @GetMapping("/showForm")
    public String showForm(Model model) {
        model.addAttribute("student", new Student());
        return "student-form";
    }

    @PostMapping("/processForm")
    public String processForm(@ModelAttribute("student") Student student) {

        System.out.println("theStudent: " + student.getFirstName() + " "
                            + student.getLastName());
        return "student-confirmation";
    }
}
```

In the `@GetMapping` method we are exposing a `Student` Class Instance to the View Template. So in a 
Thymeleaf form, we can set the fields in the class automatically. They are binded, and the setter methods 
for those fields are called while submitting the form data:

```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
    <title>Student Registration Form</title>
</head>
<body>
    <form action="#" th:action="@{processForm}" th:object="${student}" method="post">
        <p>First Name: <input type="text" th:field="*{firstName}"></p>
        <p>Last Name: <input type="text" th:field="*{lastName}"/></p>

        <input type="submit" value="Submit" />

    </form>
</body>
</html>
```

Then in the `action` URL path (`processForm`), the getter methods are called. The `Student` attribute
is exposed with `@ModelAttribute` annotation:

```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="en">
<head>
    <meta charset="UTF-8">
    <title>Student Confirmation</title>
</head>
<body>
    <p th:text="'Student is confirmed: ' + ${student.firstName} + ' ' + ${student.lastName}"></p>
</body>
</html>
```

### Form Validation Annotations

```
@NotNull
@Min
@Max
@Size
@Pattern
@Future/@Past -> Dates
...
```

Sample:

* Object shipped in model, with validation annotations:
```
public class Student {

    @NotNull(message="is required")
    @Size(min=1, message="is required")
    private String firstName;

    @NotNull(message="is required")
    @Size(min=1, message="is required")
    private String lastName;

    @NotNull(message="is required")
    @Min(value=18, message="Age must be grater than 18")
    @Max(value=150, message="Nobody has lived so long")
    private int age;

```

* Controller Validation (With @Valid annotation):

```
@Controller
@RequestMapping("/student")
public class StudentController {

    @GetMapping("/showForm")
    public String showForm(Model model) {
        model.addAttribute("student", new Student());
        return "student-form";
    }

    @PostMapping("/processForm")
    public String processForm(
            @Valid @ModelAttribute("student") Student student,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "student-form";
        }
        return "student-confirmation";
    }
}
```

_Note the condition to return one template or another._


Add a condition also to show error message in the thymeleaf template (`span` tag):

```
</head>
<body>
    <form action="#" th:action="@{processForm}" th:object="${student}" method="post">
        <p>First Name (*): <input type="text" th:field="*{firstName}" />
            <span th:if="${#fields.hasErrors('firstName')}" th:errors="*{firstName}" style="color: red" ></span>
        </p>
        ...
        ...
```

* There is some logic wrong here with the form `action`, since when the validation fails, the url is changed. 
To be investigated...


### Custom Validators (Custom Annotations)

It's possible to create custom annotations in order to validate form fields:

```
    ...
    @CourseCode(value = "COU")
    private String courseCode;
    ...
```

`@CourseCode` is a custom validation annotation. So we first we need an interface for it:

```
@Constraint(validatedBy = CourseCodeConstraintValidator.class)
@Target( {ElementType.METHOD, ElementType.FIELD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface CourseCode {

    public String value() default "ZDR";

    public String message() default "must start with ZDR";
    
    public Class<?>[] groups() default {};
    
    public Class<? extends Payload>[] payload() default {}; 
}
```

Where:

- `@Constraint` - Annotation takes the Class name with the validation business logic.
- `@Target` - Takes the relation of Java elements to which the annotation can be applied.
- `@Retention` - For how long the annotation can be introspected, etc...

Then Annotation's parameters are configured with defaults (`value`, `message`)

We also need `groups` and `payload` method signatures:
- `groups` - Not pretty sure what is this.
- `payload` - Provide custom details about validation failure such as severity, error code, etc...

Now for the `ConstraintValidator`, its method `isValid()` must return a `boolean` indicating 
whether the validation succeeds or not:

```
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
```

Where:
- `ConstraintValidator<CourseCode, String>` - Takes the annotation name (`CourseCode` and the type of object 
we'll be validating against.
- `initialize` - This method receives the annotation object, so it's possible to extract information.
- `isValid` - This method receives the actual field we want to validate. It must return a `boolean` indicating 
whether the validation succeeds or not. In this case we only check if the code is not null and starts with the code 
indicated as `value` parameter in the custom annotation.

### @InitBinder Annotation

This processes all requests received by the controller. The annotated method is always executed.

I.e: This example removes leading/trailing whitespaces from the form data introduced. If all characters are
whitespaces, it converts the value to `null`:

```
@InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }
```

