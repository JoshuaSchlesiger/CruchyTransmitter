package de.milschschnitte.crunchytransmitter.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;

import de.milschschnitte.crunchytransmitter.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NotificationService {

    static Logger logger = LogManager.getLogger(NotificationService.class);

    public static void sendNotificationInBlocks(String notificationTitle, String body, Integer animeId) {
        List<String> tokens = DatabaseManager.getAllTokensForAnime(animeId);
        int blockSize = 500;

        for (int i = 0; i < tokens.size(); i += blockSize) {
            List<String> blockTokens = tokens.subList(i, Math.min(i + blockSize, tokens.size()));

            sendNotificationBlock(notificationTitle, body, blockTokens);
        }

        logger.info("Sended all notification to users");
    }

    private static void sendNotificationBlock(String notificationTitle, String body, List<String> tokens) {
        List<Message> messages = new ArrayList<>();

        for (String token : tokens) {
            Message message = Message.builder()
                    .setNotification(Notification.builder().setTitle(notificationTitle).setBody(body).build())
                    .setToken(token)
                    .build();
            messages.add(message);
        }

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEach(messages);
            for (int i = 0; i < response.getResponses().size(); i++) {
                SendResponse sendResponse = response.getResponses().get(i);
                if (sendResponse.isSuccessful()) {
                    logger.info("Notification batch " + i + " sent successfully");
                } else {
                    logger.error("Failed to send notification batch " + i + ": " + sendResponse.getException());
                }
            }
        } catch (FirebaseMessagingException e) {
            logger.error("Error while sending notification batch: " + e.getMessage());
        }
    }
}