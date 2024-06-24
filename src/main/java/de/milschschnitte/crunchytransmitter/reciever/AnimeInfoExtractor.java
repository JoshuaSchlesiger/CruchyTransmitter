package de.milschschnitte.crunchytransmitter.reciever;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AnimeInfoExtractor {
    static Logger logger = LogManager.getLogger(AnimeInfoExtractor.class);
        
    public static List<Anime> extractAnime(String html, EnumWeekdays weekday) {
        // Parse the HTML content
        Document doc = Jsoup.parse(html);

        List<Anime> animeList = new ArrayList<>();

        // Extracting datanull;
        Elements rows = doc.select("tr");
        List<String> imageUrlList = new ArrayList<String>();
        List<String> crunchyrollUrlList = new ArrayList<String>();

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

                        String crunchyrollUrl = col.select("a").attr("href");
                        crunchyrollUrl = crunchyrollUrl.replace("\\\"", "").replace("\\\\", "\\");
                        crunchyrollUrlList.add(crunchyrollUrl);
                    }else if(i == 1){
                        Episode episode = anime.getEpisode();
                        episode.setDateOfWeekday(weekday);
    
                        Elements strongElements = col.select("strong");
    
                        if (!strongElements.isEmpty()) {
                            String title = "";
                            for (Element sElement : strongElements) {
                                title += sElement.text() + " ";
                            }
    
                            anime.setTitle(title);
    
                            if(j == 0) {
                                anime.setImageUrl(imageUrlList.get(j));
                                anime.setCrunchyrollUrl(crunchyrollUrlList.get(j));
                            }else if (j == 1) {
                                anime.setImageUrl(imageUrlList.get(j));
                                anime.setCrunchyrollUrl(crunchyrollUrlList.get(j));
                            }
    
                            animeList.add(anime);
                        }
                    }else if(i == 2){
                        Elements pElements = col.select("p");
                        if (!pElements.isEmpty() && animeList.size() != 0) {
                            String episodeRaw = pElements.size() > 0 ? pElements.get(0).text() : "";
                            String time = pElements.size() > 1 ? pElements.get(1).text() : "";
    
                            String correctionDate = null;
                            if(pElements.size() > 2 && time.endsWith("*"))correctionDate = pElements.get(2).text();
                                 
                            String episodeString = null;
                            int length = episodeRaw.length();
                            if(length < 4) {
                                episodeString = episodeRaw;
                            }
    
                            if(episodeString == null){
                                int index = episodeRaw.indexOf('F');
                                if (index != -1) {
                                    episodeString = episodeRaw.substring(index, length - 3);
                                } else {
                                    logger.info("Faulty episodeString 2: " + episodeRaw);
                                    episodeString = "";
                                }
                            }

                            Anime animeBuffer = animeList.get(animeList.size() - (cols.size() - j));
                            Episode episodeBuffer = animeBuffer.getEpisode();
                            if(episodeBuffer.getEpisode() == "") episodeBuffer.setEpisodes(episodeString);
                            if(episodeBuffer.getReleaseTime() == null) episodeBuffer.setReleaseTime(time);
                            if(correctionDate != null) episodeBuffer.setDateOfCorretionDate(correctionDate);
    
                        } else {
                            logger.warn("No p elements found in this td. This is not good");
                        }
                    }

                }
            } else {
                // System.out.println("No td elements found in this tr.");
            }
        }
        return animeList;
    }
}
