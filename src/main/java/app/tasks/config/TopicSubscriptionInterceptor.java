package app.tasks.config;

import app.tasks.model.ShareModel;
import app.tasks.repository.ShareRepository;
import app.tasks.service.AuthService;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

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
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor headerAccessor= StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
//            Principal userPrincipal = headerAccessor.getUser();
            if (!validateSubscription(message, headerAccessor.getDestination())) {
//                throw new IllegalArgumentException("No permission for this topic");
//                throw new MessagingException("No permission");
                return null;
            }
        }
        return message;
    }

    private boolean validateSubscription(Message<?> message, String topicDestination) {
        if(topicDestination!= null && topicDestination.contains("/topic/task/")){
            try {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                String sessionToken = accessor.getNativeHeader("Authorization").get(0);

                String taskId = topicDestination.substring(topicDestination.lastIndexOf('/') + 1);
                String userId = authService.isAuthenticated(sessionToken);
                Optional<ShareModel> shareModel = shareRepository.findByTaskIdAndUserId(taskId, userId);
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
