package de.milschschnitte.crunchytransmitter.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    /**
     * Init FirebaseApp (google)
     * @return
     * @throws IOException
     */
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        Resource resource = new ClassPathResource("crunchy-transmitter-firebase-adminsdk-fondb-bf8a8f2a1f.json");
        InputStream serviceAccount = resource.getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }
}