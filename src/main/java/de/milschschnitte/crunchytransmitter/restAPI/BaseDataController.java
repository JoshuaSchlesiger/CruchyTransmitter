package de.milschschnitte.crunchytransmitter.restAPI;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class BaseDataController {

    private final Map<String, Bucket> buckets;

    public BaseDataController(){
        this.buckets = new ConcurrentHashMap<>();
    }

    @GetMapping("/anime")
    public ResponseEntity<String> getAnimes(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ipAddress, k -> {
            Bandwidth limit = Bandwidth.classic(5, Refill.greedy(1, Duration.ofMinutes(1)));
            return Bucket.builder().addLimit(limit).build();
        });

        if(bucket.tryConsume(1)){
            return ResponseEntity.ok("lol");
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests");
    }
}
