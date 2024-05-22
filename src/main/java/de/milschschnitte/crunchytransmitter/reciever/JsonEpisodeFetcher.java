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
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JsonEpisodeFetcher {
    public void fff() {
        String url = "https://cr-news-api-service.prd.crunchyrollsvc.com/v1/de-DE/stories?slug=seasonal-lineup%2F2024%2F4%2F1%2Fcrunchyroll-wochenprogramm-fruehling-2024"; // Ersetze
                                                                                                                                                                            // durch
                                                                                                                                                                            // die
                                                                                                                                                                            // tatsächliche
                                                                                                                                                                            // URL

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
                        JsonNode rootNode = objectMapper.readTree(result.toString());

                        JsonNode bodyNode = rootNode.get("story").get("content").get("body");

                        if (bodyNode.isArray()) {
                            FileWriter writer = new FileWriter("output.json");

                            for (int i = 6; i < bodyNode.size(); i++) {
                                JsonNode elementNode = bodyNode.get(i);
                                writer.write(elementNode.toPrettyString());

                                try {
                                    JsonNode weekdayNode = elementNode.get("content").get("content").get(0).get("content").get(0).get("text");
                                    EnumWeekdays weekday = EnumWeekdays.fromGermanName(weekdayNode.asText());
                                    if (weekday == null){ break;}
                                    
                                    System.out.println(weekday.getGermanName()); 
                                    continue;
                                } catch (NullPointerException e) {
                                    System.out.println("skip weekday");
                                }
                                try {
                                    JsonNode animeEpisodeNode = elementNode.get("items");
                                    for (JsonNode animeEpisode : animeEpisodeNode) {
                                        // String animeHTML = animeEpisode.get("table").get("content").toString();
                                        // System.out.println(animeHTML);
                                    }
                                    continue;
                                } catch (NullPointerException e) {
                                    System.out.println("skip animeEpisode");
                                }
                                try {
                                    JsonNode horizontalLineNode = elementNode.get("content").get("content").get(0).get("type");
                                    continue;
                                } catch (NullPointerException e) {
                                    System.out.println("skip horizontal");
                                }                          
                            }
                            writer.close();
                        } else {
                            System.out.println("Body of Crunchyroll list is not an array");
                        }
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
