package ru.parser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.parser.config.TelegramBotProperties;
import ru.parser.model.Company;
import ru.parser.telegram.TelegramBotConstants;
import ru.parser.telegram.TelegramBotMessages;
import ru.parser.telegram.TelegramMessageFormatter;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TelegramBotService implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    private final TelegramBotProperties telegramBotProperties;
    private final TelegramClient telegramClient;

    public TelegramBotService(TelegramBotProperties telegramBotProperties) {
        this.telegramBotProperties = telegramBotProperties;
        this.telegramClient = createTelegramClient();
        greet();
    }

    private TelegramClient createTelegramClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        TelegramBotProperties.Proxy proxyConfig = telegramBotProperties.getProxy();
        if (proxyConfig.isEnabled() && proxyConfig.getHost() != null && !proxyConfig.getHost().isEmpty()) {
            Proxy proxy = new Proxy(Proxy.Type.SOCKS,
                    new InetSocketAddress(proxyConfig.getHost(), proxyConfig.getPort()));
            clientBuilder.proxy(proxy);

            if (proxyConfig.getUsername() != null && !proxyConfig.getUsername().isEmpty()) {
                clientBuilder.proxyAuthenticator((route, response) -> {
                    String credential = Credentials.basic(proxyConfig.getUsername(), proxyConfig.getPassword());
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                });
            }

            logger.info("Telegram bot SOCKS5 proxy enabled: {}:{}", proxyConfig.getHost(), proxyConfig.getPort());
        } else {
            logger.info("Telegram bot proxy disabled");
        }

        return new OkHttpTelegramClient(clientBuilder.build(), getBotToken());
    }

    @Override
    public String getBotToken() {
        return telegramBotProperties.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Long userId = update.getMessage().getFrom().getId();
                String messageText = update.getMessage().getText();

                if (!isUserAllowed(userId)) {
                    logger.warn("Unauthorized access attempt from user ID: {}", userId);
                    sendMessage(userId, TelegramBotMessages.Access.UNAUTHORIZED);
                    return;
                }

                if (messageText.equals(TelegramBotConstants.Commands.START)) {
                    sendMessage(userId, TelegramBotMessages.Access.GREETING);
                } else if (messageText.equals(TelegramBotConstants.Commands.STATUS)) {
                    sendMessage(userId, TelegramBotMessages.Responses.BOT_WORKING);
                } else if (messageText.equals(TelegramBotConstants.Commands.HELP)) {
                    sendMessage(userId, TelegramBotMessages.Responses.HELP_TEXT);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing update", e);
        }
    }

    public void sendMessageToAdmin(String message) {
        Long adminId = telegramBotProperties.getAdminId();
        if (adminId != null) {
            sendMessage(adminId, message);
        }
    }

    public void sendMessageToAllUsers(String message) {
        sendMessageToAllUsers(message, false);
    }

    public void sendMessageToAllUsers(String message, boolean useMarkdown) {
        Long adminId = telegramBotProperties.getAdminId();
        if (adminId != null) {
            sendMessage(adminId, message, useMarkdown);
        }

        if (telegramBotProperties.getAllowedIds() != null) {
            for (Long userId : telegramBotProperties.getAllowedIds()) {
                if (!userId.equals(adminId)) {
                    sendMessage(userId, message, useMarkdown);
                }
            }
        }
    }

    private void sendMessage(Long userId, String message) {
        sendMessage(userId, message, false);
    }

    private void sendMessage(Long userId, String message, boolean useMarkdown) {
        try {
            SendMessage.SendMessageBuilder builder = SendMessage.builder()
                    .chatId(userId.toString())
                    .text(message);

            if (useMarkdown) {
                builder.parseMode(TelegramBotConstants.Format.MARKDOWN_V2);
            }

            telegramClient.execute(builder.build());
            logger.info("Message sent to user {}", userId);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message to user {}: {}", userId, e.getMessage());
        }
    }

    private boolean isUserAllowed(Long userId) {
        Long adminId = telegramBotProperties.getAdminId();
        if (adminId != null && adminId.equals(userId)) {
            return true;
        }

        if (telegramBotProperties.getAllowedIds() != null) {
            return telegramBotProperties.getAllowedIds().contains(userId);
        }

        return false;
    }

    public boolean isEnabled() {
        return telegramBotProperties.getToken() != null && !telegramBotProperties.getToken().isEmpty();
    }

    private void greet() {
        if (isEnabled()) {
            sendMessageToAdmin(TelegramBotMessages.System.APP_STARTED);
        }
    }

    
    public void sendReport(List<Company> newOnes) {
        if (isEnabled()) {
            String scanCompleteMessage = TelegramMessageFormatter.formatScanCompleteMessage(newOnes.size());
            sendMessageToAllUsers(scanCompleteMessage, false);

            if (!newOnes.isEmpty()) {
                int batchSize = TelegramBotConstants.Batch.COMPANY_BATCH_SIZE;
                int totalCompanies = newOnes.size();

                for (int i = 0; i < totalCompanies; i += batchSize) {
                    int endIndex = Math.min(i + batchSize, totalCompanies);
                    List<Company> batch = newOnes.subList(i, endIndex);

                    // Форматируем сообщение с порцией компаний
                    String batchMessage = TelegramMessageFormatter.formatCompanyBatch(batch, i, totalCompanies);
                    sendMessageToAllUsers(batchMessage, true);

                    // Небольшая задержка между сообщениями, чтобы не превысить лимиты Telegram API
                    if (i + batchSize < totalCompanies) {
                        try {
                            Thread.sleep(TelegramBotConstants.Batch.MESSAGE_DELAY_MS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            logger.error("Interrupted while sleeping between messages", e);
                            break;
                        }
                    }
                }
            }
        }
    }
}
