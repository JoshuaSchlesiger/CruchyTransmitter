package de.milschschnitte.crunchytransmitter.reciever;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JsonEpisodeFetcher {
    public void fff(){
        String url = "https://cr-news-api-service.prd.crunchyrollsvc.com/v1/de-DE/stories?slug=seasonal-lineup%2F2024%2F4%2F1%2Fcrunchyroll-wochenprogramm-fruehling-2024"; // Ersetze durch die tatsächliche URL
        System.out.println(url);
        // Erzeuge HttpClient
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Erzeuge HTTP GET Anfrage
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Prüfe den Statuscode der Antwort
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Extrahiere den Body der Antwort
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        // Lese den Inhalt der Antwort
                        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        // Konvertiere den JSON String zu einem JsonNode
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNode = objectMapper.readTree(result.toString());
                        System.out.println(jsonNode.get("story").get("uuid").asText());
                        // Verarbeite das JSON (zum Beispiel, gib es aus)
                        // System.out.println(jsonNode.toPrettyString());
                    }
                } else {
                    System.out.println("Fehler: " + statusCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
