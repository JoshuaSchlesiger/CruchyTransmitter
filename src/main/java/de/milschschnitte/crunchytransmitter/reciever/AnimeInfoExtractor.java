package de.milschschnitte.crunchytransmitter.reciever;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AnimeInfoExtractor {
    public static List<Anime> extractAnime(String html, EnumWeekdays weekday) {
        // Parse the HTML content
        Document doc = Jsoup.parse(html);

        List<Anime> animeList = new ArrayList<>();

        // Extracting datanull;
        Elements rows = doc.select("tr");
        List<String> imageUrlList = new ArrayList<String>();

        //The 1st and 2nd and 3rd only have relevant information
        /* Structure explaned
         * tr   images
         * tr   td Anime    td Anime 
         * tr   td EpisodeI  td EpisodeI
         * tr   useless shit ...
         */
        for (int i = 0; i <= 3; i++) {
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

                    if(i == 0){
                        String imageUrl = col.select("img").attr("src");
                        imageUrlList.add(imageUrl);
                        //ImageUrl muss noch in die anime rein
                        continue;
                    }


                    Episode episode = anime.getEpisode();
                    episode.setDateOfWeekday(weekday);

                    Elements aElements = col.select("a");

                    if (!aElements.isEmpty()) {
                        String title = "";
                        for (Element aElement : aElements) {
                            title += aElement.text() + " ";
                        }

                        anime.setTitle(title);

                        if(j == 0) {
                            anime.setImageUrl(imageUrlList.get(j));
                        }else if (j == 1) {
                            anime.setImageUrl(imageUrlList.get(j));
                        }

                        animeList.add(anime);
                        continue;
                    }

                    Elements pElements = col.select("p");
                    if (!pElements.isEmpty() && animeList.size() != 0) {
                        String episodeRaw = pElements.size() > 0 ? pElements.get(0).text() : "Keine Episodeninfos";
                        String time = pElements.size() > 1 ? pElements.get(1).text() : "Keine Zeitinfos";

                        String correctionDate = null;
                        if(time.endsWith("*")) correctionDate = pElements.get(2).text();

                        int length = episodeRaw.length();
                        if(length < 4) continue;
                        String episodeString = episodeRaw.substring(3, length - 3);

                        Anime animeBuffer = animeList.get(animeList.size() - (cols.size() - j));
                        Episode episodeBuffer = animeBuffer.getEpisode();
                        if(episodeBuffer.getEpisode() == "") episodeBuffer.setEpisodes(episodeString);
                        if(episodeBuffer.getReleaseTime() == null) episodeBuffer.setReleaseTime(time);
                        if(correctionDate != null) episodeBuffer.setDateOfCorretionDate(correctionDate);

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
