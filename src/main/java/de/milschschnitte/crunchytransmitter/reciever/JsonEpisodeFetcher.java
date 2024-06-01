package de.milschschnitte.crunchytransmitter.reciever;

import org.apache.http.HttpEntity;
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

    public List<Anime> fetch(String seasonURL) {
        List<Anime> animeList = new ArrayList<Anime>();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(seasonURL);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                        StringBuilder result = new StringBuilder();

                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

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

                                //Check weekdays, continue until monday is not found
                                try {
                                    JsonNode weekdayNode = elementNode.get("content").get("content").get(0).get("content").get(0).get("text");
                                    weekday = EnumWeekdays.fromGermanName(weekdayNode.asText());
                                    if (weekday == null) continue;
                                    if (weekday == EnumWeekdays.MONDAY) mondayFound = true;

                                    continue;
                                } catch (NullPointerException e) {
                                    // System.out.println("skip weekday");
                                }
                                if(mondayFound == false) continue;

                                try {
                                    JsonNode animeEpisodeNode = elementNode.get("items");
                                    for (JsonNode animeEpisode : animeEpisodeNode) {
                                        String animeHTML = animeEpisode.get("table").get("content").toString();
                                        animeList.addAll(AnimeInfoExtractor.extractAnime(animeHTML, weekday));
                                    }
                                    continue;
                                } catch (NullPointerException e) {
                                    // System.out.println("skip animeEpisode");
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
                            throw new RuntimeException();
                        }
                    }
                } else {
                    System.out.println("Error: " + statusCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return animeList;
    }
}
