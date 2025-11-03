package abeshutt.staracademy.item.data.card;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.math.random.RandomSource;
import abeshutt.staracademy.math.roll.NumberRoll;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BoosterPackEntry implements ISerializable<NbtCompound, JsonObject> {

    private Identifier modelBase;
    private Identifier modelRipped;
    private int color;
    private final List<Pool> pools;

    public BoosterPackEntry() {
        this.pools = new ArrayList<>();
    }

    public BoosterPackEntry(Identifier modelBase, Identifier modelRipped, int color, Pool... pools) {
        this.modelBase = modelBase;
        this.modelRipped = modelRipped;
        this.color = color;
        this.pools = new ArrayList<>(Arrays.asList(pools));
    }

    public Identifier getModelBase() {
        return this.modelBase;
    }

    public Identifier getModelRipped() {
        return this.modelRipped;
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject()).map(json -> {
            Adapters.IDENTIFIER.writeJson(this.modelBase).ifPresent(tag -> json.add("modelBase", tag));
            Adapters.IDENTIFIER.writeJson(this.modelRipped).ifPresent(tag -> json.add("modelRipped", tag));
            Adapters.INT.writeJson(this.color).ifPresent(tag -> json.add("color", tag));

            JsonArray array = new JsonArray();

            for(Pool pool : this.pools) {
               pool.writeJson().ifPresent(array::add);
            }

            json.add("pools", array);
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        this.modelBase = Adapters.IDENTIFIER.readJson(json.get("modelBase")).orElseThrow();
        this.modelRipped = Adapters.IDENTIFIER.readJson(json.get("modelRipped")).orElseThrow();
        this.color = Adapters.INT.readJson(json.get("color")).orElseThrow();
        JsonArray pools = json.get("pools").getAsJsonArray();
        this.pools.clear();

        for(JsonElement element : pools) {
           Pool pool = new Pool();
           pool.readJson(element.getAsJsonObject());
           this.pools.add(pool);
        }
    }

    public List<CardData> generate(RandomSource random) {
        List<CardData> cards = new ArrayList<>();

        for(Pool pool : this.pools) {
            if(random.nextFloat() >= pool.probability) continue;
            int count = pool.count.get(random).intValue();

            for(int i = 0; i < count; i++) {
                String id = ModConfigs.CARD_ENTRIES.flatten(pool.entry, random).orElse(null);
                if(id == null) continue;

                ModConfigs.CARD_ENTRIES.get(id).ifPresent(entry -> {
                    cards.add(entry.generate(random));
                });
            }
        }

        return cards;
    }

    public static class Pool implements ISerializable<NbtCompound, JsonObject> {
        private String entry;
        private NumberRoll count;
        private float probability;

        public Pool() {

        }

        public Pool(String entry, NumberRoll count, float probability) {
            this.entry = entry;
            this.count = count;
            this.probability = probability;
        }

        @Override
        public Optional<JsonObject> writeJson() {
            return Optional.of(new JsonObject()).map(json -> {
                Adapters.UTF_8.writeJson(this.entry).ifPresent(tag -> json.add("entry", tag));
                Adapters.NUMBER_ROLL.writeJson(this.count).ifPresent(tag -> json.add("count", tag));
                Adapters.FLOAT.writeJson(this.probability).ifPresent(tag -> json.add("probability", tag));
                return json;
            });
        }

        @Override
        public void readJson(JsonObject json) {
            this.entry = Adapters.UTF_8.readJson(json.get("entry")).orElseThrow();
            this.count = Adapters.NUMBER_ROLL.readJson(json.get("count")).orElseThrow();
            this.probability = Adapters.FLOAT.readJson(json.get("probability")).orElseThrow();
        }
    }

}
