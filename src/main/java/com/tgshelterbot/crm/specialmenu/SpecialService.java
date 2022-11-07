package com.tgshelterbot.crm.specialmenu;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.crm.InlineBuilder;
import com.tgshelterbot.crm.MessageSender;
import com.tgshelterbot.crm.SupportService;
import com.tgshelterbot.model.*;
import com.tgshelterbot.repository.AnimalReportRepository;
import com.tgshelterbot.repository.AnimalReportTypeRepository;
import com.tgshelterbot.repository.UserStateRepository;
import com.tgshelterbot.service.UserService;
import com.tgshelterbot.service.impl.ReportServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Optional;

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
    private final ReportServiceImpl reportService;

    /**
     * Метод обрабатывает сообщения, если у пользователя запущен специальный статус
     *
     * @param user   user
     * @param update telegram update
     * @return SendMessage, или null когда специального статуса нет и мы идем по базовой логике
     */
    public EditMessageText checkSpecialStatus(@NotNull User user, Update update) {
        if (user.getStateId() == null) {
            return null;
        }
        String tag = null;
        if (update.callbackQuery() != null) {
            tag = update.callbackQuery().data();
        }

        UserStateSpecial stateSpecial = user.getStateId().getTagSpecial();

        String message = "Что то пошло не так, не найдено сценария обработки. Нажмите /start";
        if (update.message() != null) {
            message = update.message().text();
        }
        //Даем меню с выбором приюта
        if (stateSpecial.equals(SELECT_SHELTER) && tag != null) {
            user.setShelter(Long.parseLong(tag));
            userService.update(user);
            messageSender.sendMessage(startMenu.getEditMessageStartMenu(user), user);
            return null;
        }

        //Обработка телефона
        if (stateSpecial.equals(GET_PHONE_STARTED)) {
            deleteOldMenu(user);
            user.setPhone(message);
            userService.update(user);
            bot.execute(new SendMessage(user.getTelegramId(), "Thx!!!! " + message));
            messageSender.sendMessage(startMenu.getSendMessageStartMenu(user), user);
            return null;
        }


        //Обработка переписки в чате
        if (stateSpecial.equals(SUPPORT_CHAT_STARTED)) {
            bot.execute(supportService.sendToSupport(update, user));
            return null;
        }

        if (stateSpecial.equals(REPORT_STARTED) && tag != null) {
            //Обработка отчетов, была нажата кнопка с типом отчета
            reportService.processWithTag(user, tag);
            return null;
        }

        if (stateSpecial.equals(REPORT_STARTED) && tag == null) {
            //Обработка отчетов, пустой тэг, в юзере берем тип отчета который ждем
            reportService.processNullTag(user, update);
        }
        return null;
    }


    /**
     * Метод обрабатывает первичное нажатие из inline меню, и запускает процесс уже специальной обработки
     *
     * @param user   user
     * @param update update
     * @param menu   InlineMenu
     * @return SendMessage
     */
    public EditMessageText checkSpecialStatusInMenu(@NotNull User user, Update update, InlineMenu menu) {
        EditMessageText editMessageText = new EditMessageText(user.getTelegramId(), user.getLastResponseStatemenuId().intValue(),
                "Что то пошло не так, не найдено сценария обработки. Нажмите /start");
        UserStateSpecial stateSpecial = user.getStateId().getTagSpecial();

        String tag = "";
        if (update.callbackQuery() != null) {
            tag = update.callbackQuery().data();
        }

        if (stateSpecial.equals(GET_PHONE)) {
            deleteOldMenu(user);
            UserState userState = getUserState(GET_PHONE_STARTED);
            user.setStateId(userState);
            userService.update(user);
            SendMessage sendMessage = new SendMessage(user.getTelegramId(), menu.getAnswer());
            messageSender.sendMessage(sendMessage, user);
            return null;
        }


        //Обработка начала чата
        if (stateSpecial.equals(SUPPORT_CHAT)) {
            deleteOldMenu(user);
            UserState userState = getUserState(SUPPORT_CHAT_STARTED);
            user.setStateId(userState);
            userService.update(user);

            SendMessage sendMessage = new SendMessage(user.getTelegramId(), "Для завершения чата нажмите кнопку EXIT")
                    .replyMarkup(new ReplyKeyboardMarkup(
                            new KeyboardButton("\uD83D\uDD1A EXIT"))
                            .resizeKeyboard(true)
                            .selective(true));

            bot.execute(sendMessage);
            return null;

        }

        if (stateSpecial.equals(REPORT)) {
            //Обработка отчетов
            UserState userState = getUserState(REPORT_STARTED);
            user.setStateId(userState);

            //---------------
            Animal animal = reportService.getAnimal(user);

            //Если нет за сегодня, тогда генерируем
            Optional<AnimalReport> animalReportOptional = animalReportRepository.findFirstByStateAndAnimalOrderById(AnimalReportStateEnum.CREATED, animal.getId());
            Long reportId = null;
            if (animalReportOptional.isEmpty()) {
                LinkedHashSet<AnimalReportType> report = animalReportTypeRepository.getReportSetByAnimalType(animal.getAnimalTypeId(), user.getShelter(), user.getLanguage());
                reportId = reportService.generateReport(animal.getId(), user, report);
                user.setReportId(reportId);
            } else {
                reportId = animalReportOptional.get().getId();
                user.setReportId(reportId);
            }
            LinkedHashSet<AnimalReportType> reportTypeSet = animalReportTypeRepository.findCreatedUserReport(animal.getAnimalTypeId(),
                    user.getShelter(),
                    user.getLanguage(),
                    reportId
            );

            //Когда заполнили все отчеты
            if (reportTypeSet.size() == 0) {
                deleteOldMenu(user);
                bot.execute(new SendMessage(user.getTelegramId(), "Спасибо, вы заполнили все отчеты."));
                SendMessage sendMessageStartMenu = startMenu.getSendMessageStartMenu(user);
                messageSender.sendMessage(sendMessageStartMenu, user);
                return null;
            }
            //Генерируем меню
            InlineKeyboardMarkup inlineMenuReport = inlineBuilder.getInlineMenuReport(reportTypeSet);

            EditMessageText editInlineMessageText = new EditMessageText(user.getTelegramId(),
                    user.getLastResponseStatemenuId().intValue(),
                    "Отчет по питомцу: " + animal.getName() + "\n" +
                            "выберите пункт меню, для отправки отчета").replyMarkup(inlineMenuReport);
            messageSender.sendMessage(editInlineMessageText, user);
            return null;

        }


        return editMessageText;
    }

    private UserState getUserState(UserStateSpecial stateSpecial) {
        return userStateRepository.findFirstByTagSpecial(stateSpecial).orElse(null);
    }

    private void deleteOldMenu(User user) {
        if (user.getLastResponseStatemenuId() != null) {
            DeleteMessage deleteMessage = new DeleteMessage(user.getTelegramId(), user.getLastResponseStatemenuId().intValue());
            bot.execute(deleteMessage);
        }
    }

}
