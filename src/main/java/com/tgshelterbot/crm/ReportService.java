package com.tgshelterbot.crm;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.crm.specialmenu.StartMenu;
import com.tgshelterbot.model.*;
import com.tgshelterbot.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final AnimalReportRepository animalReportRepository;
    private final AnimalReportDataRepository animalReportDataRepository;
    private final AnimalReportSetupRepository animalReportSetupRepository;
    private final AnimalReportSetupReportTypeRepository animalReportSetupReportTypeRepository;
    private final AnimalReportTypeRepository animalReportTypeRepository;
    private final AnimalRepository animalRepository;
    private final MessageForSendRepository messageForSendRepository;
    private final MessageSender messageSender;
    private final SupportService supportService;
    private final StartMenu startMenu;
    private final UserService userService;
    private final TelegramBot bot;
    private final FileService fileService;
    private final InlineBuilder inlineBuilder;
    private final LocalizedMessages lang;

    /**
     * Поиск всех животного у пользователя
     * @param user User
     * @return Animal
     */
    public Animal getAnimal(User user) {
        List<Animal> animalsByUserId = animalRepository.findAnimalsByUserId(user.getTelegramId());
        Optional<Animal> animalOptional = animalsByUserId.stream()
                .filter(animal -> animal.getState().equals(Animal.AnimalStateEnum.IN_TEST))
                .findFirst();
        return animalOptional.orElse(null);
    }

    public LinkedHashSet<AnimalReportType> getAnimalReportType() {
        return null;
    }

    //Обработка отчетов, была нажата кнопка с типом отчета
    public void processWithTag(User user, String tag) {
        Optional<AnimalReportType> firstByTagCallback = animalReportTypeRepository.findFirstByTagCallback(tag);
        if (firstByTagCallback.isEmpty()) {
            return;
        }
        AnimalReportType reportType = firstByTagCallback.get();
        //Сейвим в пользователя текущий репорт, кнопка которого была нажата
        user.setReportId(reportType.getId());

        messageSender.deleteOldMenu(user);
        InlineKeyboardMarkup menuExit = inlineBuilder.getInlineMenuExit();
        SendMessage sendMessage = new SendMessage(user.getTelegramId(), reportType.getName()).replyMarkup(menuExit);
        messageSender.sendMessage(sendMessage, user);

    }

    public void processNullTag(User user, Update update) {
        boolean isOkType = false;
        Animal animal = getAnimal(user);
        if (animal == null) {
            messageSender.sendMessage(new SendMessage(user.getTelegramId(), lang.get("start", user)), user);
            return;
        }
        Optional<AnimalReportType> optionalAnimalReportType = animalReportTypeRepository.findById(user.getReportId());
        if (optionalAnimalReportType.isEmpty()) {
            log.error("optionalAnimalReportType.isEmpty()");
            return;
        }
        Optional<AnimalReport> optionalAnimalReport = animalReportRepository.findFirstByStateAndAnimalOrderById(AnimalReportStateEnum.CREATED, animal.getId());
        if (optionalAnimalReport.isEmpty()) {
            log.error("optionalAnimalReport.isEmpty()");
            return;
        }

        AnimalReportType animalReportType = optionalAnimalReportType.get();
        AnimalReport animalReport = optionalAnimalReport.get();

        Optional<AnimalReportData> optionalAnimalReportData = animalReportDataRepository.findCurrentReport(animalReport.getId(), animalReportType.getId());
        if (optionalAnimalReportData.isEmpty()) {
            log.error("optionalAnimalReportData.isEmpty()");
            return;
        }
        AnimalReportData currentReport = optionalAnimalReportData.get();

        if (currentReport == null) {
            return;
        }

        // TODO проверка на тип отчета/валидацию размера?
        //fileService.validateSize() валидируем по размеру
        String telegramFileId = fileService.getTelegramFileId(update);
        if (telegramFileId != null) {
            if (animalReportType.getIsFile()) {
                // сохраняем фото / файл, получаем локальное имя файла
                String localPathTelegramFile = fileService.getLocalPathTelegramFile(update);
                currentReport.setLocalFileName(localPathTelegramFile);
                //опционально сейвим в базу? а нужно ли?!
                currentReport.setReportDataFile(fileService.getLocalFile(localPathTelegramFile));
                currentReport.setTgMessageId(update.message().messageId().longValue()); // id сообщения потом для форвардинга
                //TODO может сейвить и telegramFileId?
                currentReport.setState(AnimalReportStateEnum.WAIT);
                animalReportDataRepository.save(currentReport);
                supportService.sendToSupport(update, user);
                isOkType = true;
            } else
                //Обработаем дальше, что не верный тип
                isOkType = false;
        }

        //Проверка на тип "Text"
        if (!isOkType && update.message() != null && update.message().text() != null && animalReportType.getIsText()) {
            currentReport.setState(AnimalReportStateEnum.WAIT);
            currentReport.setReportText(update.message().text());
            animalReportDataRepository.save(currentReport);
            isOkType = true;
        }

        //Обработка
        if (isOkType) {
            // все хорошо выкидываем снова меню с отчетом
            LinkedHashSet<AnimalReportType> reportSetByAnimalType = animalReportTypeRepository.findCreatedUserReport(
                    animal.getAnimalTypeId(),
                    user.getShelter(),
                    user.getLanguage(),
                    animalReport.getId()
            );
            log.debug("AnimalTypeId={}\n AnimalReport.getId={}", animal.getAnimalTypeId(), animalReport.getId());
            //Проверка может все отчеты уже заполнили
            if (reportSetByAnimalType.size() == 0) {
                animalReport.setState(AnimalReportStateEnum.WAIT);
                animalReportRepository.save(animalReport);
                user.setReportId(null);
                messageSender.deleteOldMenu(user);
                bot.execute(new SendMessage(user.getTelegramId(), "\uD83C\uDF89 Спасибо, вы заполнили все отчеты."));
                SendMessage sendMessageStartMenu = startMenu.getSendMessageStartMenu(user);
                messageSender.sendMessage(sendMessageStartMenu, user);
                return;
            }

            //Отфильтруем которые в статусе создан
            InlineKeyboardMarkup inlineMenuReport = inlineBuilder.getInlineMenuReport(reportSetByAnimalType);
            messageSender.deleteOldMenu(user);
            user.setReportId(null); //при успешной отправке отчета
            SendMessage sendMessage = new SendMessage(user.getTelegramId(),
                    animalReportType.getTextIsGoodContent()).replyMarkup(inlineMenuReport);
            messageSender.sendMessage(sendMessage, user);
        }

        if (!isOkType) {
            messageSender.deleteOldMenu(user);
            InlineKeyboardMarkup inlineMenuExit = inlineBuilder.getInlineMenuExit();
            SendMessage sendMessage = new SendMessage(user.getTelegramId(), animalReportType.getTextIsBadContent()).replyMarkup(inlineMenuExit);
            messageSender.sendMessage(sendMessage, user);
        }


    }

    public AnimalReport generateReport(Animal animal, User user) {
        //Вставим отчет в базу
        AnimalReport animalReport = new AnimalReport();
        animalReport.setState(AnimalReportStateEnum.CREATED);
        animalReport.setAnimal(animal.getId());
        animalReport.setUserId(user.getTelegramId());
        animalReport.setDtCreate(OffsetDateTime.now());
        AnimalReport save = animalReportRepository.save(animalReport);
        Long animalReportId = save.getId();
        //сохраним айди отчета в текущего отчета в пользователя
        user.setReportId(animalReportId);

        LinkedHashSet<AnimalReportType> reportSetByAnimalType = animalReportTypeRepository.getReportSetByAnimalType(animal.getAnimalTypeId(),
                user.getShelter(),
                user.getLanguage()
        );

        //Генерируем типы
        for (AnimalReportType animalReportType : reportSetByAnimalType) {
            AnimalReportData reportData = new AnimalReportData();
            reportData.setTelegramUser(user.getTelegramId());
            reportData.setAnimalReport(animalReport);
            reportData.setReportType(animalReportType);
            reportData.setState(AnimalReportStateEnum.CREATED);
            reportData.setDtCreate(OffsetDateTime.now());
            animalReportDataRepository.save(reportData);
        }
        userService.update(user);
        return animalReport;
    }

    /**
     * Вызываем в планировщике, закрывает старые отчеты которые пользователь не заполнил корректно.
     * Отправляем сообщение пользователю, что он балбес
     */
    @Transactional
    public void runClosureScheduler() {
        // Выбираем из базы не закрытые отчеты
        OffsetDateTime offsetDateTime = OffsetDateTime.now().minusDays(2);
        List<AnimalReport> reportList = animalReportRepository.findAllByStateAndDtCreateBefore(AnimalReportStateEnum.CREATED, offsetDateTime);
        for (AnimalReport animalReport : reportList) {
            Long userId = animalReport.getUserId();
            log.error("reportList {}", animalReport);
            animalReportDataRepository.updateSetCloseOldReport(animalReport.getId());
            animalReport.setState(AnimalReportStateEnum.REJECTED);
            animalReportRepository.save(animalReport);

            // Отправляем сообщение в планировщик отправки сообщений. Можем установить отправку только в разумное время, допустим после 10 утра
            String message = "У Вас отклонен отчет за дату: " +
                    animalReport.getDtCreate().truncatedTo(ChronoUnit.DAYS).format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault()))
                    + " по времени сдачи отчета. Будьте ответственнее. Надеюсь Вы забываете только отправлять отчеты, а не кормить питомца!";
            log.debug("message to user: {}, {}", userId, message);
            MessageForSend messageForSend = new MessageForSend();
            messageForSend.setUserId(userId);
            messageForSend.setDeleted(false);
            messageForSend.setDtNeedSend(OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS).plusSeconds(10));
            messageForSend.setText(message);
            //Так же можно отправить и в чат волонтерам.
            messageForSendRepository.save(messageForSend);
        }
    }
}
