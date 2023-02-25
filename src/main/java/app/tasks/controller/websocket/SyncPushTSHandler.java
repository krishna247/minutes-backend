package app.tasks.controller.websocket;

import app.tasks.model.websocket.UpdateSyncModel;
import app.tasks.service.TaskService;
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

    private final SimpMessagingTemplate template;
    private final SimpUserRegistry userRegistry;
    private final TaskService taskService;

    public SyncPushTSHandler(SimpMessagingTemplate template, SimpUserRegistry userRegistry, TaskService taskService) {
        this.template = template;
        this.userRegistry = userRegistry;
        this.taskService = taskService;
    }

    @Scheduled(fixedDelay=5000)
    @Async
    public void publishUpdates() throws ExecutionException, InterruptedException, TimeoutException {
        userRegistry.getUsers().stream()
                .map(SimpUser::getName)
                .forEach(System.out::println);
        Set<SimpUser> usersList = userRegistry.getUsers();
//        template.convertAndSend("/topic/greetings", new TestModel("haha"));
        for(SimpUser user: usersList) {
            // TODO scale using ThreadPoolExecutor
            String userId = Arrays.stream(user.getName().split("-")).toList().get(0);
            var lastUpdateTs = taskService.getMaxUpdateTsAsync(userId);
            template.convertAndSendToUser(user.getName(),"/topic/MaxUpdateTs",
                    new UpdateSyncModel(lastUpdateTs.get(2L, TimeUnit.SECONDS)));
        }}
}
