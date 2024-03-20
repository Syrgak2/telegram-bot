package pro.sky.telegrambot.repository;

import liquibase.pro.packaged.L;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    List<NotificationTask> findBySendDate(LocalDateTime executionDate);
}
