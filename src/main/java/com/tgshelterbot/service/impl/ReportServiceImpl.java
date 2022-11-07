package com.tgshelterbot.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.tgshelterbot.crm.InlineBuilder;
import com.tgshelterbot.crm.MessageSender;
import com.tgshelterbot.crm.SupportService;
import com.tgshelterbot.crm.specialmenu.StartMenu;
import com.tgshelterbot.model.*;
import com.tgshelterbot.repository.*;
import com.tgshelterbot.service.FileService;
import com.tgshelterbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl {

    private final AnimalReportRepository animalReportRepository;
    private final AnimalReportDataRepository animalReportDataRepository;
    private final AnimalReportSetupRepository animalReportSetupRepository;
    private final AnimalReportSetupReportTypeRepository animalReportSetupReportTypeRepository;
    private final AnimalReportTypeRepository animalReportTypeRepository;
    private final AnimalRepository animalRepository;
    private final MessageSender messageSender;
    private final SupportService supportService;
    private final StartMenu startMenu;
    private final UserService userService;
    private final TelegramBot bot;
    private final FileService fileService;
    private final InlineBuilder inlineBuilder;


    public Animal getAnimal(User user) {
        List<Animal> animalsByUserId = animalRepository.findAnimalsByUserId(user.getTelegramId());
        return animalsByUserId.stream()
                .filter(animal -> animal.getState().equals(Animal.AnimalStateEnum.IN_TEST))
                .findFirst().orElseThrow(EntityNotFoundException::new);
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

        deleteOldMenu(user);
        InlineKeyboardMarkup menuExit = inlineBuilder.getInlineMenuExit();
        SendMessage sendMessage = new SendMessage(user.getTelegramId(), reportType.getName()).replyMarkup(menuExit);
        messageSender.sendMessage(sendMessage, user);

    }

    public void processNullTag(User user, Update update) {
        boolean isOkType = false;
        Animal animal = getAnimal(user);
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
                deleteOldMenu(user);
                bot.execute(new SendMessage(user.getTelegramId(), "Спасибо, вы заполнили все отчеты."));
                SendMessage sendMessageStartMenu = startMenu.getSendMessageStartMenu(user);
                messageSender.sendMessage(sendMessageStartMenu, user);
                return;
            }

            //Отфильтруем которые в статусе создан
            InlineKeyboardMarkup inlineMenuReport = inlineBuilder.getInlineMenuReport(reportSetByAnimalType);
            deleteOldMenu(user);
            user.setReportId(null); //при успешной отправке отчета
            SendMessage sendMessage = new SendMessage(user.getTelegramId(),
                    animalReportType.getTextIsGoodContent()).replyMarkup(inlineMenuReport);
            messageSender.sendMessage(sendMessage, user);
        }

        if (!isOkType) {
            deleteOldMenu(user);
            InlineKeyboardMarkup inlineMenuExit = inlineBuilder.getInlineMenuExit();
            SendMessage sendMessage = new SendMessage(user.getTelegramId(), animalReportType.getTextIsBadContent()).replyMarkup(inlineMenuExit);
            messageSender.sendMessage(sendMessage, user);
        }


    }

    public Long generateReport(Long animalId, User user, LinkedHashSet<AnimalReportType> reportSetByAnimalType) {
        //Вставим отчет в базу
        AnimalReport animalReport = new AnimalReport();
        animalReport.setState(AnimalReportStateEnum.CREATED);
        animalReport.setAnimal(animalId);
        animalReport.setDtCreate(OffsetDateTime.now());
        AnimalReport save = animalReportRepository.save(animalReport);
        Long animalReportId = save.getId();
        //сохраним айди отчета в текущего отчета в пользователя
        user.setReportId(animalReportId);
        //Генерируем типы
        reportSetByAnimalType.forEach(animalReportType -> {
            AnimalReportData reportData = new AnimalReportData();
            reportData.setTelegramUser(user.getTelegramId());
            reportData.setAnimalReport(animalReport);
            reportData.setReportType(animalReportType);
            reportData.setState(AnimalReportStateEnum.CREATED);
            reportData.setDtCreate(OffsetDateTime.now());
            animalReportDataRepository.save(reportData);
        });
        userService.update(user);
        return animalReportId;
    }


    private void deleteOldMenu(User user) {
        if (user.getLastResponseStatemenuId() != null) {
            DeleteMessage deleteMessage = new DeleteMessage(user.getTelegramId(), user.getLastResponseStatemenuId().intValue());
            bot.execute(deleteMessage);
        }
    }


}
