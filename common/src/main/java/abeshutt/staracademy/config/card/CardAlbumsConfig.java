package abeshutt.staracademy.config.card;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.card.CardAlbumEntry;
import abeshutt.staracademy.config.FileConfig;
import com.google.gson.annotations.Expose;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CardAlbumsConfig extends FileConfig {

    @Expose private Map<String, CardAlbumEntry> values;

    @Override
    public String getPath() {
        return "card.albums";
    }

    public Map<String, CardAlbumEntry> getValues() {
        return this.values;
    }

    public Optional<CardAlbumEntry> get(String id) {
        return Optional.ofNullable(this.values.get(id));
    }

    @Override
    protected void reset() {
        this.values = new LinkedHashMap<>();
        this.values.put("base", new CardAlbumEntry(
                StarAcademyMod.id("card_album/base"),
                9031664));
    }

}
