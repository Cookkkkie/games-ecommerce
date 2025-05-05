package com.ecommerce.recommendations.repository;

import com.ecommerce.recommendations.entity.Game;
import com.ecommerce.recommendations.utility.TfidfVectorizer;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class DataLoader {

    /** raw names + metadata */
    @Value("${steam_names.data.path}")
    private String namesParquetLocation;

    /** cleaned text for vectorizing */
    @Value("${steam.data.path}")
    private String cleanedParquetLocation;

    private final ResourceLoader resourceLoader;
    public DataLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    // exposed for display/look-ups
    @Getter private List<Game> rawGames;
    @Getter private Map<Integer, Integer> rawIndexOfGame;

    // exposed for modeling
    @Getter private List<Game> cleanGames;
    @Getter private Map<Integer, Integer> cleanIndexOfGame;
    @Getter private TfidfVectorizer vectorizer;

    @PostConstruct
    public void init() throws IOException {
        // ─── load raw parquet ────────────────────────────────────────────────────
        rawGames       = new ArrayList<>();
        rawIndexOfGame = new HashMap<>();
        readParquet(
                namesParquetLocation,
                rawGames,
                rawIndexOfGame
        );

        // ─── load cleaned parquet ──────────────────────────────────────────────
        cleanGames       = new ArrayList<>();
        cleanIndexOfGame = new HashMap<>();
        readParquet(
                cleanedParquetLocation,
                cleanGames,
                cleanIndexOfGame
        );

        // ─── build TF-IDF on the cleaned documents ─────────────────────────────
        vectorizer = new TfidfVectorizer(
                cleanGames.stream()
                        .map(Game::document)
                        .toList()
        );
    }

    /**
     * Generic helper: read a parquet @path into the given list & index map.
     */
    private void readParquet(
            String resourcePath,
            List<Game> targetGames,
            Map<Integer,Integer> targetIndex
    ) throws IOException {
        Resource res = resourceLoader.getResource(resourcePath);
        Path    p   = new Path(res.getURI());

        try (ParquetReader<GenericRecord> reader =
                     AvroParquetReader.<GenericRecord>builder(p)
                             .withConf(new Configuration())
                             .build()) {

            GenericRecord rec;
            while ((rec = reader.read()) != null) {
                Game g = toGame(rec);
                targetIndex.put(g.getGameId(), targetGames.size());
                targetGames.add(g);
            }
        }
    }

    /** Convert an Avro record into your Game POJO */
    private static Game toGame(GenericRecord r) {
        return Game.builder()
                .gameId    (Integer.parseInt(r.get("gameId").toString()))
                .name      (optStr(r, "name"))
                .about     (optStr(r, "about_the_game"))
                .categories(optStr(r, "categories"))
                .genres    (optStr(r, "genres"))
                .publishers(optStr(r, "publishers"))
                .price     (optDbl(r, "price"))
                .build();
    }

    private static String optStr(GenericRecord r, String f) {
        Object o = r.get(f);
        return o == null ? "" : o.toString();
    }
    private static double optDbl(GenericRecord r, String f) {
        Object o = r.get(f);
        return o == null ? Double.NaN : Double.parseDouble(o.toString());
    }
}
