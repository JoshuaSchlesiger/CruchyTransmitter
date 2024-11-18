package de.milschschnitte.crunchytransmitter.reciever;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.milschschnitte.crunchytransmitter.ConfigLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for extracting the information from the json provided by the
 * Crunchyroll website
 */
public class JsonEpisodeFetcher {
    private Logger logger = LoggerFactory.getLogger(JsonEpisodeFetcher.class);
    private List<Anime> animeList = new ArrayList<Anime>();

    /**
     * 
     * @param seasonURL
     * @return List<Anime> animeList
     */
    public List<Anime> fetchAndProcess() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(ConfigLoader.getProperty("crunchyroll.seasonURL"));
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        processEntity(entity);
                    }
                } else {
                    logger.warn("Error on Crunchyroll-Serveraccess: " + statusCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (animeList.size() == 0) {
            logger.error("Did not find any animes after process");
        }

        return animeList;
    }

    private void processEntity(HttpEntity entity) throws UnsupportedOperationException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
        StringBuilder result = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        ObjectMapper objectMapper = new ObjectMapper();

        // Main json
        JsonNode rootNode = objectMapper.readTree(result.toString());
        // Body of JSON
        JsonNode bodyNode = rootNode.get("story").get("content").get("body");
        if (bodyNode.isArray()) {
            // Storing current weekday for processing
            EnumWeekdays weekday = null;

            // Used to skip ever element util monday is there
            Boolean mondayFound = false;

            // Idk why i star with 1, maybe 0 is possible too ?
            for (int i = 1; i < bodyNode.size(); i++) {
                JsonNode elementNode = bodyNode.get(i);

                // Check weekdays, continue until monday is found
                try {
                    JsonNode weekdayNode = elementNode.get("content").get("content").get(0).get("content").get(0)
                            .get("text");

                    // Filter end of sunday
                    if (weekdayNode.asText().equals("Katalogtitel")) {
                        break;
                    }

                    weekday = EnumWeekdays.fromGermanName(weekdayNode.asText());
                    // Filter everything until monday
                    if (weekday == null)
                        continue;

                    if (weekday == EnumWeekdays.MONDAY)
                        mondayFound = true;
                    continue;
                } catch (NullPointerException e) {
                }

                if (mondayFound == false)
                    continue;

                try {
                    JsonNode animeEpisodeNode = elementNode.get("items");
                    for (JsonNode animeEpisode : animeEpisodeNode) {
                        JsonNode animeItem = animeEpisode.get("content");

                        try {
                            Anime anime = AnimeInfoExtractor.extractAnime(animeItem, weekday);
                            if (anime != null)
                                animeList.add(anime);
                        } catch (Exception e) {
                            logger.warn("SHIT : ", e);
                        }
                    }

                } catch (NullPointerException e) {
                    logger.warn("Exception occurred while processing entity", e);
                }
            }
        } else {
            logger.error("Major problem at processing json of crunchyroll");
        }
    }
}
