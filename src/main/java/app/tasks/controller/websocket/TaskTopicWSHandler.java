package app.tasks.controller.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class TaskTopicWSHandler {

    @Autowired
    private SimpUserRegistry userRegistry;
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(@Payload String message, Principal principal) {
        System.out.println(principal.getName());
        System.out.println(userRegistry.getUserCount());
        return message;
    }
}
