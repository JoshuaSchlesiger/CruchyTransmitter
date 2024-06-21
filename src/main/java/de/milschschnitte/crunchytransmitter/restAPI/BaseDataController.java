package de.milschschnitte.crunchytransmitter.restAPI;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.milschschnitte.crunchytransmitter.ConfigLoader;
import de.milschschnitte.crunchytransmitter.ScheduledTasks;
import de.milschschnitte.crunchytransmitter.database.DatabaseManager;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class BaseDataController {

    Logger logger = LogManager.getLogger(BaseDataController.class);
    private final Map<String, Bucket> buckets;

    public BaseDataController() {
        this.buckets = new ConcurrentHashMap<>();
    }

    @GetMapping("/anime")
    public ResponseEntity<String> getAnimes(HttpServletRequest request) {
        logger.info("Access to /anime Website");
        String ipAddress = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ipAddress, k -> {
            Bandwidth limit = Bandwidth.classic(
                    Integer.valueOf(ConfigLoader.getProperty("spring.api.animeget.storage")),
                    Refill.greedy(Integer.valueOf(ConfigLoader.getProperty("spring.api.animeget.reffill")),
                            Duration.ofMinutes(1)));
            return Bucket.builder().addLimit(limit).build();
        });

        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(ScheduledTasks.json);
        }

        logger.warn("Someone is greeeeeedy at get: " + ipAddress);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests");
    }

    @PostMapping("/registerToken")
    public ResponseEntity<String> postFCMToken(HttpServletRequest request,
            @RequestBody FCMTokenPostRequest requestBody) {
        String password = requestBody.getPassword();
        String ipAddress = request.getRemoteAddr();

        if (password == null || !password.equals(ConfigLoader.getProperty("spring.api.key"))) {
            logger.info("Someone tried to register with wrong password. IP-Address: " + ipAddress + "on registerToken");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        Bucket bucket = buckets.computeIfAbsent(ipAddress, k -> {
            Bandwidth limit = Bandwidth.classic(Integer.valueOf(ConfigLoader.getProperty("spring.api.token.storage")), Refill.greedy(Integer.valueOf(ConfigLoader.getProperty("spring.api.token.refill")), Duration.ofMinutes(1)));
            return Bucket.builder().addLimit(limit).build();
        });

        if (bucket.tryConsume(1)) {
            DatabaseManager.setToken(requestBody.getToken());
            return ResponseEntity.ok("successful");
        }

        logger.warn("Someone is greeeeeedy at post registerToken: " + ipAddress);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests");
    }


    @PostMapping("/updateAnimeSub")
    public ResponseEntity<String> postAnimeSub(HttpServletRequest request,
            @RequestBody AnimeSubPostRequest requestBody) {
        String password = requestBody.getPassword();
        String ipAddress = request.getRemoteAddr();

        if (password == null || !password.equals(ConfigLoader.getProperty("spring.api.key"))) {
            logger.info("Someone tried to register with wrong password. IP-Address: " + ipAddress + "on updateAnimeSub");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        Bucket bucket = buckets.computeIfAbsent(ipAddress, k -> {
            Bandwidth limit = Bandwidth.classic(Integer.valueOf(ConfigLoader.getProperty("spring.api.animesub.storage")), Refill.greedy(Integer.valueOf(ConfigLoader.getProperty("spring.api.animesub.refill")), Duration.ofMinutes(1)));
            return Bucket.builder().addLimit(limit).build();
        });

        if (bucket.tryConsume(1)) {
            String result = DatabaseManager.changeAnimeSub(requestBody.getToken() , requestBody.getAnimeID());
            return ResponseEntity.ok(result);
        }

        logger.warn("Someone is greeeeeedy at post updateAnimeSub: " + ipAddress);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests");
    }
}
