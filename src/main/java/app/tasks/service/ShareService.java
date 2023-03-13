package app.tasks.service;

import app.tasks.repository.ShareRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShareService {
    ShareRepository shareRepository;

    public ShareService(ShareRepository shareRepository){
        this.shareRepository = shareRepository;
    }

    public void deleteAllSharesForTask(String taskId){
        shareRepository.deleteByTaskIdIn(List.of(taskId));
    }
}
