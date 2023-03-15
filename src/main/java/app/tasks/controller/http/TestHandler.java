package app.tasks.controller.http;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;

@RestController
public class TestHandler {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public TestHandler(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @GetMapping("/test")
    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    public void test(@Autowired HttpServletRequest request, @RequestHeader("Authorization") String sessionToken){
        for (Iterator<String> it = request.getHeaderNames().asIterator(); it.hasNext(); ) {
            String header = it.next();
            System.out.println(header+":"+request.getHeader(header));
        }
}}
