//package app.tasks.controller.websocket;
//
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.Map;
//
//@Service
//public class ServerTimeTsHandler {
//    private final SimpMessagingTemplate simpMessagingTemplate;
//
//    public ServerTimeTsHandler(SimpMessagingTemplate simpMessagingTemplate) {
//        this.simpMessagingTemplate = simpMessagingTemplate;
//    }
//
//    @Scheduled(fixedRate = 3000)
//    public void sendMessage(){
//        var result = Map.of("time",new Date());
//        simpMessagingTemplate.convertAndSend("/topic/time",result);
//    }
//}
