package de.milschschnitte.crunchytransmitter.reciever;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AnimeInfoExtractor {
    public static List<Anime> extractAnime(String html, String weekday) {
        // Parse the HTML content
        Document doc = Jsoup.parse(html);

        List<Anime> animeList = new ArrayList<>();

        // Extracting data
        Elements rows = doc.select("tr");
        //The 2nd and 3rd only have relevant information

        /* Structure explaned
         * tr   useless shit ...
         * tr   td Anime    td Anime 
         * tr   td EpisodeI  td EpisodeI
         * tr   useless shit ...
         */
        for (int i = 1; i <= 3; i++) {
            Element row;
            try {
                row = rows.get(i);
            } catch (Exception e) {
                break;
            }
            
            Elements cols = row.select("td");

            if (cols.size() > 0) {
                for( int j = 0; j < cols.size(); j++ ) {
                    Element col = cols.get(j);

                    Anime anime = new Anime();
                    anime.setWeekday(weekday);

                    Elements aElements = col.select("a");

                    if (!aElements.isEmpty()) {
                        String title = "";
                        for (Element aElement : aElements) {
                            title += aElement.text() + " ";
                        }

                        anime.setTitle(title);
                        animeList.add(anime);
                        continue;
                    }

                    Elements pElements = col.select("p");
                    if (!pElements.isEmpty() && animeList.size() != 0) {
                        String episodeRaw = pElements.size() > 0 ? pElements.get(0).text() : "No episode info";
                        String time = pElements.size() > 1 ? pElements.get(1).text() : "No time info";

                        String correctionDate = null;
                        if(time.endsWith("*")) correctionDate = pElements.get(2).text();

                        int length = episodeRaw.length();
                        if(length < 4) continue;
                        String episode = episodeRaw.substring(5, length - 3);

                        Anime animeObject = animeList.get(animeList.size() - (cols.size() - j));
                        if(animeObject.getEpisode() == "") animeObject.setEpisodes(episode);
                        if(animeObject.getReleaseTime() == "") animeObject.setReleaseTime(time);
                        if(correctionDate != null) animeObject.setCorretionDate(correctionDate);

                    } else {
                        // System.out.println("No p elements found in this td.");
                    }
                }
            } else {
                // System.out.println("No td elements found in this tr.");
            }
        }
        return animeList;
    }
}
