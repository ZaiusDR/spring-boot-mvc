package info.esuarez.springmvcdemo.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api")
public class HelloWorldController {

    @GetMapping("/showForm")
    public String showForm() {
        return "helloworld-form";
    }

    @GetMapping("/processForm")
    public String processForm() {
        return "helloWorld";
    }

    /*
    @GetMapping("/processVersionTwo")
    public String letsShoutDude(HttpServletRequest request, Model model) {
        String studentName = request.getParameter("studentName");
        studentName = studentName.toUpperCase();
        String result = "Yo! " + studentName;

        model.addAttribute("message", result);

        return "helloWorld";
    }
    */

    @GetMapping("/processVersionTwo")
    public String letsShoutDude(
            @RequestParam("studentName") String studentName,
            Model model) {

        studentName = studentName.toUpperCase();
        String result = "Yo! " + studentName;

        model.addAttribute("message", result);

        return "helloWorld";
    }
}
