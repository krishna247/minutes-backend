package app.tasks.config.ws;

import app.tasks.model.ShareModel;
import app.tasks.model.websocket.StompPrincipal;
import app.tasks.repository.ShareRepository;
import app.tasks.service.AuthService;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Component
public class TopicSubscriptionInterceptor implements ChannelInterceptor {
    final AuthService authService;
    final
    ShareRepository shareRepository;

    public TopicSubscriptionInterceptor(AuthService authService, ShareRepository shareRepository) {
        this.authService = authService;
        this.shareRepository = shareRepository;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor headerAccessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(StompCommand.CONNECT.equals(headerAccessor.getCommand())){
            String sessionToken = headerAccessor.getNativeHeader("Authorization").get(0);
            String userId = authService.isAuthenticated(sessionToken);
            Principal user = new StompPrincipal(userId,sessionToken);
            headerAccessor.setUser(user);
            headerAccessor.setLeaveMutable(true);
        }
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            if (!validateSubscription(message, headerAccessor)) {
                return null;
            }
        }
        return message;
    }

    @SuppressWarnings({"ConstantConditions"})
    private boolean validateSubscription(Message<?> message, StompHeaderAccessor headerAccessor) {
        String topicDestination = headerAccessor.getDestination();
        if(topicDestination!= null && topicDestination.contains("/topic/task/")){
            try {
                String sessionToken = headerAccessor.getNativeHeader("Authorization").get(0);

                String taskId = topicDestination.substring(topicDestination.lastIndexOf('/') + 1);
                String userId = authService.isAuthenticated(sessionToken);
                Optional<ShareModel> shareModel = shareRepository.findByTaskIdAndUserId(taskId, userId);
                System.out.println("shareModel: "+shareModel.isPresent());
                return shareModel.isPresent();
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                return false;
            }
        }
        if(topicDestination!= null && topicDestination.contains("/topic/user/")){
            try {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                String sessionToken = accessor.getNativeHeader("Authorization").get(0);

                String subUserId = topicDestination.substring(topicDestination.lastIndexOf('/') + 1);
                String userId = authService.isAuthenticated(sessionToken);
                return subUserId.equals(userId);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

}
