package app.tasks.controller.http;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldHandler {
    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring Boot";
    }
}
