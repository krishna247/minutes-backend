package app.tasks.controller.websocket;

import app.tasks.model.websocket.TestModel;
import app.tasks.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class SyncPushTSHandler {

    @Autowired
    private SimpMessagingTemplate template;
    @Autowired private SimpUserRegistry userRegistry;
    @Autowired private TaskService taskService;

    @Scheduled(fixedDelay=5000)
    @Async
    public void publishUpdates() throws ExecutionException, InterruptedException, TimeoutException {
        userRegistry.getUsers().stream()
                .map(SimpUser::getName)
                .forEach(System.out::println);
        Set<SimpUser> usersList = userRegistry.getUsers();
//        template.convertAndSend("/topic/greetings", new TestModel("haha"));
        for(SimpUser user: usersList) {
            String userId = Arrays.stream(user.getName().split("-")).toList().get(0);
            var lastUpdateTs = taskService.getMaxUpdateTsAsync(userId);
            template.convertAndSendToUser(user.getName(),"/topic/MaxUpdateTs", new TestModel(lastUpdateTs.get(2L, TimeUnit.SECONDS).toString()));
        }}
}
