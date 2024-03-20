package pro.sky.telegrambot.service;

import liquibase.pro.packaged.A;
import liquibase.pro.packaged.L;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

@Service
public class NotificationTaskService {
    @Autowired
    NotificationTaskRepository notificationTaskRepository;

    public NotificationTask save(NotificationTask notificationTask) {
        return notificationTaskRepository.save(notificationTask);
    }

    public NotificationTask edite(NotificationTask notificationTask) {
        return notificationTaskRepository.save(notificationTask);
    }

    public NotificationTask findById(Long id) {
        return notificationTaskRepository.findById(id).get();
    }

    public void removeBiId(Long id) {
        notificationTaskRepository.deleteById(id);
    }
}
