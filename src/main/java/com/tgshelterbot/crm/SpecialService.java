package com.tgshelterbot.crm;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.crm.specialmenu.StartMenu;
import com.tgshelterbot.model.*;
import com.tgshelterbot.repository.AnimalReportRepository;
import com.tgshelterbot.repository.AnimalReportTypeRepository;
import com.tgshelterbot.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Locale;

import static com.tgshelterbot.model.UserStateSpecial.*;

@Service
@RequiredArgsConstructor
public class SpecialService {
    private final Logger log = LoggerFactory.getLogger(SpecialService.class);

    private final UserStateRepository userStateRepository;
    private final UserService userService;
    private final MessageSender messageSender;
    private final SupportService supportService;
    private final TelegramBot bot;
    private final StartMenu startMenu;
    private final InlineBuilder inlineBuilder;
    private final AnimalReportTypeRepository animalReportTypeRepository;
    private final AnimalReportRepository animalReportRepository;
    private final ReportService reportService;
    private final LocalizedMessages lang;

    /**
     * Метод обрабатывает сообщения, если у пользователя запущен специальный статус
     *
     * @param user   user
     * @param update telegram update
     */
    public void checkSpecialStatus(@NotNull User user, Update update) {
        if (user.getStateId() == null) {
            return;
        }
        String tag = null;
        if (update.callbackQuery() != null) {
            tag = update.callbackQuery().data();
        }

        UserStateSpecial stateSpecial = user.getStateId().getTagSpecial();

        String message = "";
        if (update.message() != null) {
            message = update.message().text();
        }
        //Даем меню с выбором приюта
        if (stateSpecial.equals(SELECT_SHELTER) && tag != null) {
            user.setShelter(Long.parseLong(tag));
            userService.update(user);
            messageSender.sendMessage(startMenu.getEditMessageStartMenu(user), user);
            return;
        }

        //Обработка телефона
        if (stateSpecial.equals(GET_PHONE_STARTED)) {
            messageSender.deleteOldMenu(user);
            user.setPhone(message);
            userService.update(user);
            bot.execute(new SendMessage(user.getTelegramId(), lang.get("answer_get_phone") + message));
            messageSender.sendMessage(startMenu.getSendMessageStartMenu(user), user);
            return;
        }


        //Обработка переписки в чате
        if (stateSpecial.equals(SUPPORT_CHAT_STARTED)) {
            bot.execute(supportService.sendToSupport(update, user));
            return;
        }

        if (stateSpecial.equals(REPORT_STARTED) && tag != null) {
            //Обработка отчетов, была нажата кнопка с типом отчета
            reportService.processWithTag(user, tag);
            return;
        }

        if (stateSpecial.equals(REPORT_STARTED) && tag == null) {
            //Обработка отчетов, пустой тэг, в юзере берем тип отчета который ждем
            reportService.processNullTag(user, update);
            return;
        }
        message = lang.get("start");
        SendMessage sendMessage = new SendMessage(user.getTelegramId(), message);
        messageSender.sendMessage(sendMessage, user);
    }


    /**
     * Метод обрабатывает первичное нажатие из inline меню, и запускает процесс уже специальной обработки
     *
     * @param user   user
     * @param update update
     * @param menu   InlineMenu
     */
    public void checkSpecialStatusInMenu(@NotNull User user, Update update, InlineMenu menu) {
        UserStateSpecial stateSpecial = user.getStateId().getTagSpecial();

        String tag = "";
        if (update.callbackQuery() != null) {
            tag = update.callbackQuery().data();
        }

        if (stateSpecial.equals(GET_PHONE)) {
            messageSender.deleteOldMenu(user);
            UserState userState = getUserState(GET_PHONE_STARTED);
            user.setStateId(userState);
            userService.update(user);
            SendMessage sendMessage = new SendMessage(user.getTelegramId(), menu.getAnswer());
            messageSender.sendMessage(sendMessage, user);
            return;
        }


        //Обработка начала чата
        if (stateSpecial.equals(SUPPORT_CHAT)) {
            messageSender.deleteOldMenu(user);
            UserState userState = getUserState(SUPPORT_CHAT_STARTED);
            user.setStateId(userState);
            userService.update(user);

            SendMessage sendMessage = new SendMessage(user.getTelegramId(), lang.get("chat_exit_btn", user))
                    .replyMarkup(new ReplyKeyboardMarkup(
                            new KeyboardButton("\uD83D\uDD1A"))
                            .resizeKeyboard(true)
                            .selective(true));

            bot.execute(sendMessage);
            return;

        }

        //Обработка отчетов
        if (stateSpecial.equals(REPORT)) {
            Animal animal = reportService.getAnimal(user);
            if (animal == null) {
                messageSender.deleteOldMenu(user);
                messageSender.sendMessage(new SendMessage(user.getTelegramId(), lang.get("you_dont_have_animals")), user);
                messageSender.sendMessage(startMenu.getSendMessageStartMenu(user), user);
                return;
            }
            UserState userState = getUserState(REPORT_STARTED);
            user.setStateId(userState);


            // Ищем все отчеты за сегодня и вчера, по всем статусам
            LinkedHashSet<AnimalReport> reportSet = animalReportRepository.findAllByAnimalAndDtCreateAfterOrderByDtCreate(
                    animal.getId(),
                    OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(1)
            );

            AnimalReport animalReport = new AnimalReport();
            LinkedHashSet<AnimalReportType> reportTypeSet = new LinkedHashSet<>();
            // Генерим новый если пустой
            if (reportSet.isEmpty()) {
                reportTypeSet = animalReportTypeRepository.getReportSetByAnimalType(animal.getAnimalTypeId(), user.getShelter(), user.getLanguage());
                animalReport = reportService.generateReport(animal, user);
                user.setReportId(animalReport.getId());
            }

            for (AnimalReport report : reportSet) {
                // если есть за вчера созданные выдать его
                if (report.getDtCreate().isBefore(OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS)) && report.getState().equals(AnimalReportStateEnum.CREATED)) {
                    animalReport = report;
                    break;
                }
                // если есть за сегодня, созданные выдать его
                if (report.getDtCreate().isAfter(OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS)) && report.getState().equals(AnimalReportStateEnum.CREATED)) {
                    animalReport = report;
                    break;
                }
                // если есть ожидающие за сегодня - ждем принятия, выходим
                if (report.getDtCreate().isAfter(OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS)) && report.getState().equals(AnimalReportStateEnum.WAIT)) {
                    user.setReportId(null);
                    messageSender.deleteOldMenu(user);
                    bot.execute(new SendMessage(user.getTelegramId(), lang.get("thx_all_report_complete", user)));
                    SendMessage sendMessageStartMenu = startMenu.getSendMessageStartMenu(user);
                    messageSender.sendMessage(sendMessageStartMenu, user);
                    return;
                }
                // отчет сдали и уже приняли
                if (report.getDtCreate().isAfter(OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS)) && report.getState().equals(AnimalReportStateEnum.ACCEPT)) {
                    user.setReportId(null);
                    messageSender.deleteOldMenu(user);
                    bot.execute(new SendMessage(user.getTelegramId(), lang.get("thx_all_report_complete_and_accepted", user)));
                    SendMessage sendMessageStartMenu = startMenu.getSendMessageStartMenu(user);
                    messageSender.sendMessage(sendMessageStartMenu, user);
                    return;
                }
            }
            // Генерируем репорт если его нет
            if (animalReport.getId() == null) {
                animalReport = reportService.generateReport(animal, user);
                user.setReportId(animalReport.getId());
            }
            reportTypeSet = animalReportTypeRepository.findCreatedUserReport(animal.getAnimalTypeId(),
                    user.getShelter(),
                    user.getLanguage(),
                    animalReport.getId()
            );

            //Генерируем меню
            InlineKeyboardMarkup inlineMenuReport = inlineBuilder.getInlineMenuReport(reportTypeSet);

            EditMessageText editInlineMessageText = new EditMessageText(user.getTelegramId(),
                    user.getLastResponseStatemenuId().intValue(),
                    "Дата: " + animalReport.getDtCreate().truncatedTo(ChronoUnit.DAYS).format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())) + "\n" +
                            "Отчет по питомцу: " + animal.getName() + "\n" +
                            "выберите пункт меню, для отправки отчета").replyMarkup(inlineMenuReport);
            messageSender.sendMessage(editInlineMessageText, user);
            return;

        }

        EditMessageText editMessageText = new EditMessageText(user.getTelegramId(), user.getLastResponseStatemenuId().intValue(),
                lang.get("start", user));
        messageSender.sendMessage(editMessageText, user);
    }

    private UserState getUserState(UserStateSpecial stateSpecial) {
        return userStateRepository.findFirstByTagSpecial(stateSpecial).orElse(null);
    }


}
