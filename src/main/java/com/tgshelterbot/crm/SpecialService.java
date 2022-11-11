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
    private final ReportService reportService;

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
            bot.execute(new SendMessage(user.getTelegramId(), "Thx!!!! " + message));
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
        message = "Что то пошло не так, не найдено сценария обработки. Нажмите /start";
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

            SendMessage sendMessage = new SendMessage(user.getTelegramId(), "Для завершения чата нажмите кнопку EXIT")
                    .replyMarkup(new ReplyKeyboardMarkup(
                            new KeyboardButton("\uD83D\uDD1A EXIT"))
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
                messageSender.sendMessage(new SendMessage(user.getTelegramId(), "❗️У Вас нет закрепленных животных"), user);
                messageSender.sendMessage(startMenu.getSendMessageStartMenu(user), user);
                return;
            }
            UserState userState = getUserState(REPORT_STARTED);
            user.setStateId(userState);

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
                messageSender.deleteOldMenu(user);
                bot.execute(new SendMessage(user.getTelegramId(), "\uD83C\uDF89 Спасибо, вы заполнили все отчеты."));
                SendMessage sendMessageStartMenu = startMenu.getSendMessageStartMenu(user);
                messageSender.sendMessage(sendMessageStartMenu, user);
                return;
            }
            //Генерируем меню
            InlineKeyboardMarkup inlineMenuReport = inlineBuilder.getInlineMenuReport(reportTypeSet);

            EditMessageText editInlineMessageText = new EditMessageText(user.getTelegramId(),
                    user.getLastResponseStatemenuId().intValue(),
                    "Отчет по питомцу: " + animal.getName() + "\n" +
                            "выберите пункт меню, для отправки отчета").replyMarkup(inlineMenuReport);
            messageSender.sendMessage(editInlineMessageText, user);
            return;

        }

        EditMessageText editMessageText = new EditMessageText(user.getTelegramId(), user.getLastResponseStatemenuId().intValue(),
                "Что то пошло не так, не найдено сценария обработки. Нажмите /start");
        messageSender.sendMessage(editMessageText, user);
    }

    private UserState getUserState(UserStateSpecial stateSpecial) {
        return userStateRepository.findFirstByTagSpecial(stateSpecial).orElse(null);
    }


}
