package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import liquibase.pro.packaged.N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.TelegramBotSender;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository notificationTaskRepo;

    @Autowired
    TelegramBotSender telegramBotSender;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Process your updates here
            if (update.message() != null && update.message().text().equals("/start")) {
                handleStartCommand(update);
            } else {
                saveTask(update);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }



    private void handleStartCommand(Update update) {
        String messageText = "Hello " + update.message().from().firstName();
        Long chatId = update.message().chat().id();
        telegramBotSender.sendMessage(messageText, chatId);
    }

//    ---- сохраняет task в базу данных
    private void saveTask(Update update) {
        NotificationTask notificationTask = new NotificationTask();
        String massage = update.message().text();
        Long chatId = update.message().chat().id();

        Pattern pattern = Pattern.compile("^([0-9.:\\s]{16})(\\s)([\\W+]+)");
        Matcher matcher = pattern.matcher(massage);

        if (!matcher.find()) {
            telegramBotSender.sendMessage("Я не смог сохранить задачу так как оно не соотвествует форме. \n" +
                    "(dd.MM.yyyy HH:mm task)", chatId);
        }

        notificationTask.setSendDate(LocalDateTime.parse(matcher.group(1),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        );
        notificationTask.setMessage(matcher.group(3));
        notificationTask.setChatId(update.message().chat().id());
        notificationTaskRepo.save(notificationTask);
    }



}
