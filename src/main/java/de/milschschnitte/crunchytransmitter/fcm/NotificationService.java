package de.milschschnitte.crunchytransmitter.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;

import de.milschschnitte.crunchytransmitter.database.DatabaseManager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationService {

    static Logger logger = LoggerFactory.getLogger(NotificationService.class);

    /*
     * Google fcm allows you to send in blocks, but only with a maximum of 500
     * elements per block
     */
    public static void sendNotificationInBlocks(String notificationTitle, String body, String url, Integer animeId) {
        List<String> tokens = DatabaseManager.getAllTokensForAnime(animeId);
        int blockSize = 500;

        Integer successfully = 0;

        for (int i = 0; i < tokens.size(); i += blockSize) {
            List<String> blockTokens = tokens.subList(i, Math.min(i + blockSize, tokens.size()));

            successfully += sendNotificationBlock(notificationTitle, body, url, blockTokens);
        }

        logger.warn("Sended all notification for animeID: " + animeId + " to users. Sended " + successfully + " successfully");
    }

    private static Integer sendNotificationBlock(String notificationTitle, String body, String url,
            List<String> blockTokens) {
        List<Message> messages = new ArrayList<>();

        String notificationTitle_UTF_8 = new String(notificationTitle.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);
        String body_UTF_8 = new String(body.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        for (String token : blockTokens) {
            // Bulding message for user notific.
            Message message = Message.builder()
                    .setNotification(Notification.builder().setTitle(notificationTitle_UTF_8).setBody(body_UTF_8).build())
                    .setToken(token)
                    .putData("url", url)
                    .build();
            messages.add(message);
        }

        int successfully = 0;

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEach(messages);
            for (int i = 0; i < response.getResponses().size(); i++) {
                SendResponse sendResponse = response.getResponses().get(i);
                if (sendResponse.isSuccessful()) {
                    successfully++;
                } else {
                    // I want to save storage so i delete old tokens that will not longer work
                    DatabaseManager.deleteToken(blockTokens.get(i));
                }
            }
        } catch (FirebaseMessagingException e) {
            logger.error("Error while sending notification batch: " + e.getMessage());
        }
        return successfully;
    }
}