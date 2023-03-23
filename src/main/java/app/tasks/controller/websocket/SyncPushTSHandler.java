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
import java.util.concurrent.CompletableFuture;

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

    @Scheduled(fixedDelay = 1000)
    @Async
    public void publishUpdates() {
        Set<SimpUser> usersList = userRegistry.getUsers();
        for (SimpUser user : usersList) {
            String userId = Arrays.stream(user.getName().split("-")).toList().get(0);
            CompletableFuture.supplyAsync(() -> taskService.getMaxUpdateTs(userId))
                    .thenAccept(maxUpdateTs -> template.convertAndSendToUser(user.getName(),
                            "/topic/MaxUpdateTs", new UpdateSyncModel(maxUpdateTs)));
        }
    }
}