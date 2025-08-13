package abeshutt.staracademy.card;

import abeshutt.staracademy.CardRarity;
import abeshutt.staracademy.attribute.again.Attribute;
import abeshutt.staracademy.attribute.again.AttributeContext;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.world.random.RandomSource;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;

import java.util.*;

public class CardEntry implements ISerializable<NbtCompound, JsonObject> {

    private String icon;
    private String rarity;
    private final List<Entry> modifiers;

    public CardEntry() {
        this.modifiers = new ArrayList<>();
    }

    public CardEntry(String icon, String rarity, Entry... modifiers) {
        this.icon = icon;
        this.rarity = rarity;
        this.modifiers = new ArrayList<>(Arrays.asList(modifiers));
    }

    public CardData generate(RandomSource random) {
        String icon = ModConfigs.CARD_ICONS.flatten(this.icon, random).orElse(this.icon);
        CardRarity rarity = ModConfigs.CARD_RARITIES.generate(this.rarity, random).orElse(CardRarity.COMMON);
        List<CardModifier> modifiers = new ArrayList<>();
        this.modifiers.forEach(modifier -> modifier.generate(rarity, random).ifPresent(modifiers::add));
        return new CardData(icon, rarity, 0,  modifiers);
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject()).map(object -> {
            Adapters.UTF_8.writeJson(this.icon).ifPresent(tag -> {
                object.add("icon", tag);
            });

            Adapters.UTF_8.writeJson(this.rarity).ifPresent(tag -> {
                object.add("rarity", tag);
            });

            JsonArray modifiers = new JsonArray();

            for(Entry modifier : this.modifiers) {
                modifier.writeJson().ifPresent(modifiers::add);
            }

            object.add("modifiers", modifiers);
            return object;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        this.icon = Adapters.UTF_8.readJson(json.get("icon")).orElseThrow();
        this.rarity = Adapters.UTF_8.readJson(json.get("rarity")).orElseThrow();
        this.modifiers.clear();

        if(json.get("modifiers") instanceof JsonArray array) {
            for(JsonElement element : array) {
                if(!(element instanceof JsonObject object)) continue;
                Entry entry = new Entry();
                entry.readJson(object);
                this.modifiers.add(entry);
            }
        }
    }

    public static class Entry implements ISerializable<NbtCompound, JsonObject> {
        private String modifier;
        private Set<CardRarity> rarities;
        private float probability;

        public Entry() {

        }

        public Entry(String modifier, Set<CardRarity> rarities, float probability) {
            this.modifier = modifier;
            this.rarities = rarities;
            this.probability = probability;
        }

        public Optional<CardModifier> generate(CardRarity rarity, RandomSource random) {
            if(random.nextFloat() >= this.probability) {
                return Optional.empty();
            }

            if(this.rarities != null && !this.rarities.contains(rarity)) {
                return Optional.empty();
            }

            String source = ModConfigs.CARD_MODIFIERS.flatten(this.modifier, random).orElse(this.modifier);

            return ModConfigs.CARD_MODIFIERS.get(source).map(value -> {
                Attribute<?> attribute = value.getAttribute().copy();
                attribute.populate(new AttributeContext(random));
                return new CardModifier(source, attribute);
            });
        }

        @Override
        public Optional<JsonObject> writeJson() {
            return Optional.of(new JsonObject()).map(object -> {
                Adapters.UTF_8.writeJson(this.modifier).ifPresent(tag -> {
                    object.add("modifier", tag);
                });

                if(this.rarities != null) {
                    JsonArray array = new JsonArray();

                    this.rarities.forEach(rarity -> {
                        Adapters.CARD_RARITY.writeJson(rarity).ifPresent(array::add);
                    });

                    object.add("rarities", array);
                }

                Adapters.FLOAT.writeJson(this.probability).ifPresent(tag -> {
                    object.add("probability", tag);
                });

                return object;
            });
        }

        @Override
        public void readJson(JsonObject json) {
            this.modifier = Adapters.UTF_8.readJson(json.get("modifier")).orElseThrow();

            if(json.get("rarities") instanceof JsonArray array) {
                this.rarities = new LinkedHashSet<>();

                for(JsonElement child : array) {
                    Adapters.CARD_RARITY.readJson(child).ifPresent(this.rarities::add);
                }
            } else {
                this.rarities = null;
            }

            this.probability = Adapters.FLOAT.readJson(json.get("probability")).orElseThrow();
        }
    }


}
