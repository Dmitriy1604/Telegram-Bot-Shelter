package com.tgshelterbot.crm.specialmenu;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.User;
import com.tgshelterbot.model.UserState;
import com.tgshelterbot.model.UserStateSpecial;
import com.tgshelterbot.repository.UserStateRepository;
import com.tgshelterbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static com.tgshelterbot.model.UserStateSpecial.*;

@Service
public class SpecialService {
    private final Logger log = LoggerFactory.getLogger(SpecialService.class);

    private final UserStateRepository userStateRepository;
    private final UserService userService;
    private final TelegramBot bot;
    private final StartMenu startMenu;

    public SpecialService(UserStateRepository userStateRepository, UserService userService, TelegramBot bot, StartMenu startMenu) {
        this.userStateRepository = userStateRepository;
        this.userService = userService;
        this.bot = bot;
        this.startMenu = startMenu;
    }

    /*TODO  разбить на 2 метода, START/END
     *  1 - когда еще нет меню
     *  2 - когда есть меню
     * */
    public SendMessage checkSpecialStatus(@NotNull User user, Update update, InlineMenu menu) {
        if (user.getStateId() == null) {
            return null;
        }

        UserState userState = new UserState();
        String message = "/start";
        if (update.message() != null) {
            message = update.message().text();
        }

        if (user.getStateId() != null) {
            Optional<UserState> state = userStateRepository.findById(user.getStateId());
            if (state.isPresent()) {
                userState = state.get();
            } else {
                return null;
            }
        }

        if (userState.getTagSpecial().equals(MAIN) || userState.getTagSpecial() == null) {
            return null;
        }

        if (userState.getTagSpecial().equals(GET_PHONE)) {
            /*TODO  переписать красиво*/
            Optional<UserState> state = userStateRepository.findFirstByShelterIdAndTagSpecial(user.getShelter(), GET_PHONE_END);
            if (state.isPresent()) {
                user.setStateId(state.get().getId());
                userService.update(user);
            }
            userService.update(user);
            return new SendMessage(user.getTelegramId(), menu.getAnswer());
        }
        if (userState.getTagSpecial().equals(GET_PHONE_END)) {
            //Обработка телефона
            user.setPhone(message);
            userService.update(user);
            bot.execute(new SendMessage(user.getTelegramId(), "Thx!!!! " + message));
            return startMenu.getStartMenu(user);
        }

        //Обработка начала чата
        if (userState.getTagSpecial().equals(SUPPORT_CHAT)) {
            Optional<UserState> state = userStateRepository.findFirstByShelterIdAndTagSpecial(user.getShelter(), SUPPORT_CHAT_STARTED);
            if (state.isPresent()) {
                user.setStateId(state.get().getId());
                userService.update(user);
            }
            return new SendMessage(user.getTelegramId(), "Для завершения чата нажмите кнопку EXIT")
                    .replyMarkup(new ReplyKeyboardMarkup(
                            new KeyboardButton("\uD83D\uDD1A EXIT"))
                            .resizeKeyboard(true)
                            .selective(true));

        }
        //Обработка переписки в чате
        if (userState.getTagSpecial().equals(SUPPORT_CHAT_STARTED)) {
            return new SendMessage(user.getTelegramId(), "Ваше сообщение принято, ждите, к вам уже выехали специалисты")
                    .replyMarkup(new ReplyKeyboardMarkup(
                            new KeyboardButton("\uD83D\uDD1A EXIT"))
                            .resizeKeyboard(true)
                            .selective(true));
        }
        if (userState.getTagSpecial().equals(REPORT)) {
            //Обработка отчетов
        }
        return null;
    }

}
