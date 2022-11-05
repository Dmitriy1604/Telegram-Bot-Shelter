package com.tgshelterbot.crm.specialmenu;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.crm.InlineBuilder;
import com.tgshelterbot.crm.SupportService;
import com.tgshelterbot.model.*;
import com.tgshelterbot.repository.AnimalReportTypeRepository;
import com.tgshelterbot.repository.UserStateRepository;
import com.tgshelterbot.service.FileService;
import com.tgshelterbot.service.UserService;
import com.tgshelterbot.service.impl.ReportServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import java.util.LinkedHashSet;

import static com.tgshelterbot.model.UserStateSpecial.*;

@Service
@RequiredArgsConstructor
public class SpecialService {
    private final Logger log = LoggerFactory.getLogger(SpecialService.class);

    private final UserStateRepository userStateRepository;
    private final UserService userService;
    private final SupportService supportService;
    private final TelegramBot bot;
    private final StartMenu startMenu;
    private final ShelterMenu shelterMenu;
    private final FileService fileService;
    private final InlineBuilder inlineBuilder;
    private final AnimalReportTypeRepository animalReportTypeRepository;
    private final ReportServiceImpl reportService;

    /**
     * Метод обрабатывает сообщения, если у пользователя запущен специальный статус
     *
     * @param user   user
     * @param update telegram update
     * @return SendMessage, или null когда специального статуса нет и мы идем по базовой логике
     */
    public SendMessage checkSpecialStatus(@NotNull User user, Update update) {
        if (user.getStateId() == null) {
            return null;
        }
        String tag = "";
        if (update.callbackQuery() != null) {
            tag = update.callbackQuery().data();
        }

        UserStateSpecial stateSpecial = user.getStateId().getTagSpecial();

        String message = "Что то пошло не так, не найдено сценария обработки. Нажмите /start";
        if (update.message() != null) {
            message = update.message().text();
        }
        //Даем меню с выбором приюта
        if (stateSpecial.equals(SELECT_SHELTER)) {
            user.setStateId(userStateRepository.findFirstByTagSpecial(SELECT_SHELTER_STARTED).orElse(null)); /*TODO написать эксепшены*/
            userService.update(user);
            return startMenu.getStartMenu(user);
        }
        // Выбираем приют
        if (stateSpecial.equals(SELECT_SHELTER_STARTED)) {
            deleteOldMenu(user);
            shelterMenu.updateShelter(user, tag);
            return startMenu.getStartMenu(user);
        }

        //Обработка телефона
        if (stateSpecial.equals(GET_PHONE_STARTED)) {
            user.setPhone(message);
            userService.update(user);
            bot.execute(new SendMessage(user.getTelegramId(), "Thx!!!! " + message));
            return startMenu.getStartMenu(user);
        }


        //Обработка переписки в чате
        if (stateSpecial.equals(SUPPORT_CHAT_STARTED)) {
            return supportService.sendToSupport(update, user);
        }
//        if (stateSpecial.equals(REPORT_STARTED)) {
//            //Обработка отчетов
//            String localPathTelegramFile = fileService.getLocalPathTelegramFile(update);
//            SendDocument sendDocument = fileService.sendDocument(user.getTelegramId(), localPathTelegramFile, "Спасибки, мы получили ваш отчет");
//            bot.execute(sendDocument);
//        }

        if (stateSpecial.equals(REPORT_STARTED)) {
            //Обработка отчетов
            //---------------
            Animal animal = reportService.getAnimal(user);
            LinkedHashSet<AnimalReportType> reportSetByAnimalType = animalReportTypeRepository.getReportSetByAnimalType(animal.getAnimalTypeId(), user.getShelter(), user.getLanguage());
            reportSetByAnimalType.stream().forEach(System.out::println);
            log.error("?!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            InlineKeyboardMarkup inlineMenuReport = inlineBuilder.getInlineMenuReport(reportSetByAnimalType);
            SendMessage sendMessage = new SendMessage(user.getTelegramId(), animal.getName()
                    + " - выберите пункт меню, для отправки отчета, уже что то отправляли \n"
                    + "тег:" + tag)
                    .replyMarkup(inlineMenuReport);
            return sendMessage;

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
    public SendMessage checkSpecialStatusInMenu(@NotNull User user, Update update, InlineMenu menu) {
        SendMessage sendMessage = new SendMessage(user.getTelegramId(), "Что то пошло не так, не найдено сценария обработки. Нажмите /start");
        UserStateSpecial stateSpecial = user.getStateId().getTagSpecial();

        if (stateSpecial.equals(GET_PHONE)) {
            deleteOldMenu(user);
            UserState userState = getUserState(GET_PHONE_STARTED);
            user.setStateId(userState);
            userService.update(user);
            return new SendMessage(user.getTelegramId(), menu.getAnswer());
        }


        //Обработка начала чата
        if (stateSpecial.equals(SUPPORT_CHAT)) {
            deleteOldMenu(user);
            UserState userState = getUserState(SUPPORT_CHAT_STARTED);
            user.setStateId(userState);
            userService.update(user);

            return new SendMessage(user.getTelegramId(), "Для завершения чата нажмите кнопку EXIT")
                    .replyMarkup(new ReplyKeyboardMarkup(
                            new KeyboardButton("\uD83D\uDD1A EXIT"))
                            .resizeKeyboard(true)
                            .selective(true));

        }

        if (stateSpecial.equals(REPORT)) {
            //Обработка отчетов
            UserState userState = getUserState(REPORT_STARTED);
            user.setStateId(userState);
            userService.update(user);
            //---------------
            Animal animal = reportService.getAnimal(user);
            LinkedHashSet<AnimalReportType> reportSetByAnimalType = animalReportTypeRepository.getReportSetByAnimalType(animal.getAnimalTypeId(), user.getShelter(), user.getLanguage());

            InlineKeyboardMarkup inlineMenuReport = inlineBuilder.getInlineMenuReport(reportSetByAnimalType);
            sendMessage = new SendMessage(user.getTelegramId(), animal.getName() + " - выберите пункт меню, для отправки отчета")
                    .replyMarkup(inlineMenuReport);

        }


        return sendMessage;
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
