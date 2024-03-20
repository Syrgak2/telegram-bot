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
            }
            if (compareMessage(update)) {
                saveTask(update);
            } else {
                sendMessage("Запрос не правильно задан", update.message().chat().id());
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

//    ---- каждую минуту отправляет запрос в базу с пойскам задач для отправления
//    ---- Если есть задачи вызывает метод sendMessage
    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> nowSendTasks = notificationTaskRepo.findBySendDate(now);

        nowSendTasks.forEach(tasks -> {
            sendMessage(tasks.getMessage(), tasks.getChatId());
        });
    }

    private void handleStartCommand(Update update) {
        String messageText = "Hello " + update.message().from().firstName();
        Long chatId = update.message().chat().id();
        sendMessage(messageText, chatId);
    }
    //  ---- проверяет message
    //  ---- если message соответствует к форме записи задачи возврашает true
    //  ---- иначе false
    //  ---- Формат проверки (dd.MM.yyyy HH:mm task text)
    private boolean compareMessage(Update update) {
        String massage = update.message().text();
        Pattern pattern = Pattern.compile("^([0-9.:\\s]{16})(\\s)([\\W+]+)");
        Matcher matcher = pattern.matcher(massage);
        return matcher.matches();
    }

//    ---- сохраняет task в базу данных
    private void saveTask(Update update) {
        NotificationTask notificationTask = new NotificationTask();
        String message = update.message().text();
        Pattern pattern = Pattern.compile("^([0-9.:\\s]{16})(\\s)([\\W+]+)");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            notificationTask.setSendDate(LocalDateTime.parse(matcher.group(1),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            );
            notificationTask.setMessage(matcher.group(3));
        }

        notificationTask.setChatId(update.message().chat().id());
        notificationTaskRepo.save(notificationTask);
    }

//    ---- Отправляет сообщения к указанному chatId
    private void sendMessage(String messageText, Long chatId) {
        SendMessage message = new SendMessage(chatId, messageText);
        SendResponse response = telegramBot.execute(message);
    }

}
