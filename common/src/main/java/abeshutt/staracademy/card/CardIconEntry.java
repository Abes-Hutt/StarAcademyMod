package abeshutt.staracademy.card;

import abeshutt.staracademy.CardRarity;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CardIconEntry implements ISerializable<NbtCompound, JsonObject> {

    private String name;
    private final Map<String, Entry> entries;

    public CardIconEntry() {
        this.entries = new LinkedHashMap<>();
    }

    public CardIconEntry(String name, Map<String, Entry> entries) {
        this.name = name;
        this.entries = new LinkedHashMap<>(entries);
    }

    public Optional<Entry> get(CardRarity rarity) {
        return Optional.ofNullable(this.entries.getOrDefault(rarity.name(), this.entries.get("*")));
    }

    public Optional<Identifier> getModel(CardRarity rarity) {
        return this.get(rarity).map(Entry::getModel);
    }

    public Optional<int[]> getColors(CardRarity rarity) {
        return this.get(rarity).map(Entry::getColors);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject()).map(json -> {
            Adapters.UTF_8.writeJson(this.name).ifPresent(tag -> {
                json.add("name", tag);
            });

            this.entries.forEach((rarity, entry) -> {
                entry.writeJson().ifPresent(object -> {
                    json.add(rarity, object);
                });
            });

            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        this.name = Adapters.UTF_8.readJson(json.get("name")).orElseThrow();
        this.entries.clear();

        for(String key : json.keySet()) {
            if(key.equals("name")) continue;
            Entry entry = new Entry();
            entry.readJson(json.get(key).getAsJsonObject());
            this.entries.put(key, entry);
        }
    }

    public static class Entry implements ISerializable<NbtCompound, JsonObject> {
        private Identifier model;
        private int[] colors;

        private Entry() {

        }

        public Entry(Identifier model, int... colors) {
            this.model = model;
            this.colors = colors;
        }

        public Identifier getModel() {
            return this.model;
        }

        public int[] getColors() {
            return this.colors;
        }

        @Override
        public Optional<JsonObject> writeJson() {
            return Optional.of(new JsonObject()).map(json -> {
                Adapters.IDENTIFIER.writeJson(this.model).ifPresent(tag -> {
                    json.add("model", tag);
                });

                JsonArray colors = new JsonArray();

                for(int color : this.colors) {
                    Adapters.INT.writeJson(color).ifPresent(colors::add);
                }

                json.add("colors", colors);
                return json;
            });
        }

        @Override
        public void readJson(JsonObject json) {
            this.model = Adapters.IDENTIFIER.readJson(json.get("model")).orElseThrow();

            JsonArray colors = json.get("colors").getAsJsonArray();
            this.colors = new int[colors.size()];

            for(int i = 0; i < colors.size(); i++) {
                this.colors[i] = Adapters.INT.readJson(colors.get(i)).orElseThrow();
            }
        }
    }

}
