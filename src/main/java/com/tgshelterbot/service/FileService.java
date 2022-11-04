package com.tgshelterbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.impl.FileApi;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.tgshelterbot.model.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class FileService {

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${path.to.file.folder}")
    private String fileFolder;

    private final TelegramBot bot;
    private final UserService userService;

    public FileService(TelegramBot bot, UserService userService) {
        this.bot = bot;
        this.userService = userService;
    }

    public String getLocalPathTelegramFile(Update update) {
        String fileId = getTelegramFileId(update);
        if (fileId == null) {
            return null;
        }
        Long idUser = userService.getIdUser(update);

        GetFileResponse getFileResponse = bot.execute(new GetFile(fileId));
        File file = getFileResponse.file(); // com.pengrad.telegrambot.model.File
        FileApi fileApi = new FileApi(token);

        String fullFilePath = fileApi.getFullFilePath(file.filePath());
        String localPathFile = saveFile(fullFilePath, update);
        return localPathFile;
    }

    public String getTelegramFileId(Update update) {
        if (update.message() != null && update.message().document() != null && update.message().document().fileId() != null) {
            return update.message().document().fileId();
        }
        if (update.message().photo() != null) {
            int index = update.message().photo().length - 1;
            PhotoSize[] photo = update.message().photo();
            return photo[index].fileId();
        }
        return null;
    }

    @SneakyThrows
    public String saveFile(String urlPath, Update update) {
        // https://www.baeldung.com/java-download-file#using-nio
        String pathFile = getFullLocalPathFile(urlPath, update);
        log.debug("pathFile from saveFile,{}", pathFile);
        FileOutputStream fileOutputStream = null;
        try {
            URL url = new URL(urlPath);
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            fileOutputStream = new FileOutputStream(pathFile);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException exception) {
            log.debug(exception.getMessage());
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
        return pathFile;
    }


    public String getFullLocalPathFile(String urlPath, Update update) {
        String pathFile = "";
        try {
            String separator = System.getProperties().getProperty("file.separator"); // берем какой слеш у системы
            pathFile = fileFolder + separator + userService.getIdUser(update);
            java.io.File directory = new java.io.File(pathFile);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            pathFile = pathFile + separator + getFileName(urlPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("LocalPathFile: {}", pathFile);
        return pathFile;
    }

    public String getFileName(String path) {
        String separator = "/";
        if (path.startsWith("http")) {
            separator = "/";
        } else {
            separator = System.getProperties().getProperty("file.separator");
        }
        return path.substring(path.lastIndexOf(separator) + 1);
    }

    @SneakyThrows
    public byte[] getLocalFile(String localPath){
        return Files.readAllBytes(Path.of(localPath));
    }

    public boolean validateSize(GetFileResponse fileResponse){
        /*TODO сделать проверку размера не меньше 100 кб и не больше 10 мб
        * */
        return true;
    }

    public Long  saveFileInDB(String localPath, User user){
        /*TODO логика сохранения отчета в таблицу с отчетами*/
        return 0L;
    }
    //отправка файла с диска
    public SendDocument sendDocument(long idUser, String localPath, String description){
        SendDocument request = new SendDocument(idUser, getLocalFile(localPath)).fileName(getFileName(localPath)).caption(description);
        return request;
    }

    /*
    *   byte[] bytes = getLocalFile(localPathFile);
        log.debug("localPathFile: {}", localPathFile);
        String fileName = getFileName(localPathFile);
        log.debug("fileName: {}", fileName);
        SendDocument request = new SendDocument(idUser, bytes).fileName(fileName);
        SendResponse execute = bot.execute(request);
    * */


}
