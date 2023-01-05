package app.tasks.handler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorldHandler {
    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        return "Hello Spring Boot";
}
}
