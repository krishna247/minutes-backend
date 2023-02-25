package app.tasks.controller.http;

import app.tasks.model.websocket.TestModel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestHandler {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public TestHandler(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @GetMapping("/test")
    public void test(){
        System.out.println("Here");
        simpMessagingTemplate.convertAndSend("/topic/greetings", new TestModel("Test"));
    }
}
