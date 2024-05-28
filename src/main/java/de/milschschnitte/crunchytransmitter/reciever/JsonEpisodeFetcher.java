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
import java.util.List;

public class JsonEpisodeFetcher {
    public void fff() {
        String url = "https://cr-news-api-service.prd.crunchyrollsvc.com/v1/de-DE/stories?slug=seasonal-lineup%2F2024%2F4%2F1%2Fcrunchyroll-wochenprogramm-fruehling-2024";

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
                            EnumWeekdays weekday = null;

                            Boolean mondayFound = false;

                            for (int i = 1; i < bodyNode.size(); i++) {
                                JsonNode elementNode = bodyNode.get(i);
                                writer.write(elementNode.toPrettyString());
                                try {
                                    JsonNode weekdayNode = elementNode.get("content").get("content").get(0).get("content").get(0).get("text");
                                    weekday = EnumWeekdays.fromGermanName(weekdayNode.asText());
                                    if (weekday == null) continue;
                                    if (weekday == EnumWeekdays.MONDAY) mondayFound = true;
                                    
                                    System.out.println(weekday.getGermanName()); 
                                    continue;
                                } catch (NullPointerException e) {
                                    // System.out.println("skip weekday");
                                }

                                if(mondayFound == false) continue;

                                try {
                                    JsonNode animeEpisodeNode = elementNode.get("items");
                                    for (JsonNode animeEpisode : animeEpisodeNode) {
                                        String animeHTML = animeEpisode.get("table").get("content").toString();

                                        List<Anime> animeList = AnimeInfoExtractor.extractAnime(animeHTML, weekday.getGermanName());
                                        for (Anime anime : animeList) {
                                            System.out.println(anime.toString());
                                        }
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
                                    // System.out.println("skip horizontal");
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
