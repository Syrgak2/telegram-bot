package pro.sky.telegrambot.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.TelegramBotSender;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class NotificationJob {
    private final NotificationTaskRepository notificationTaskRepo;
    private final TelegramBotSender telegramBotSender;

    public NotificationJob(NotificationTaskRepository notificationTaskRepo, TelegramBotSender telegramBotSender) {
        this.notificationTaskRepo = notificationTaskRepo;
        this.telegramBotSender = telegramBotSender;
    }

    //    ---- каждую минуту отправляет запрос в базу с пойскам задач для отправления
//    ---- Если есть задачи вызывает метод sendMessage
    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> nowSendTasks = notificationTaskRepo.findBySendDate(now);

        nowSendTasks.forEach(tasks -> {
            telegramBotSender.sendMessage(tasks.getMessage(), tasks.getChatId());
        });
    }
}
