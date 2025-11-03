package abeshutt.staracademy.config.card;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.config.FileConfig;
import abeshutt.staracademy.item.data.card.CardRarity;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.math.WeightedList;
import abeshutt.staracademy.math.random.RandomSource;
import com.google.gson.annotations.Expose;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CardRaritiesConfig extends FileConfig {

    @Expose private Map<String, Map<CardRarity, Rational>> values;
    @Expose private Map<String, Map<String, Rational>> pools;

    @Override
    public String getPath() {
        return "card.rarities";
    }

    public Optional<WeightedList<CardRarity>> get(String id) {
        return Optional.ofNullable(this.values.get(id)).map(WeightedList::of);
    }

    public Option<CardRarity> generate(String id, RandomSource random) {
        if (id.startsWith("@")) {
            Map<String, Rational> group = this.pools.get(id.substring(1));

            if (group == null) {
                return Option.absent();
            }

            WeightedList<String> weighted = WeightedList.of(group);
            return weighted.getRandom(random).mapFlat(s -> this.generate(s, random));
        }

        Map<CardRarity, Rational> group = this.values.get(id);
        WeightedList<CardRarity> weighted = WeightedList.of(group);
        return weighted.getRandom(random);
    }

    @Override
    protected void reset() {
        this.values = new LinkedHashMap<>();
        this.pools = new LinkedHashMap<>();

        this.values.put("base", Map.of(
                CardRarity.COMMON, Rational.of(24),
                CardRarity.UNCOMMON, Rational.of(16),
                CardRarity.RARE, Rational.of(10),
                CardRarity.EPIC, Rational.of(4),
                CardRarity.LEGENDARY, Rational.of(2),
                CardRarity.SHINY, Rational.ONE));
    }

}
