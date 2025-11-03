package abeshutt.staracademy.config.card;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.config.FileConfig;
import abeshutt.staracademy.item.data.card.CardEntry;
import abeshutt.staracademy.item.data.card.CardEntry.Entry;
import abeshutt.staracademy.item.data.card.CardRarity;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.math.WeightedList;
import abeshutt.staracademy.math.random.RandomSource;
import com.google.gson.annotations.Expose;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CardEntriesConfig extends FileConfig {

    @Expose private Map<String, CardEntry> values;
    @Expose private Map<String, Map<String, Rational>> pools;

    @Override
    public String getPath() {
        return "card.entries";
    }

    public Optional<CardEntry> get(String id) {
        return Optional.ofNullable(this.values.get(id));
    }

    public Option<String> flatten(String id, RandomSource random) {
        if(id.startsWith("@")) {
            Map<String, Rational> group = this.pools.get(id.substring(1));

            if(group == null) {
                return Option.absent();
            }

            WeightedList<String> weighted = WeightedList.of(group);
            return weighted.getRandom(random).mapFlat(s -> this.flatten(s, random));
        }

        return this.values.containsKey(id) ? Option.present(id) : Option.absent();
    }

    @Override
    protected void reset() {
        this.values = new LinkedHashMap<>();
        this.pools = new LinkedHashMap<>();

        this.values.put("bulbasaur", new CardEntry("001bulbasaur", "base",
                new Entry("@all", null, 1.0F),
                new Entry("@special", Set.of(CardRarity.LEGENDARY), 1.0F)));

        this.values.put("ivysaur", new CardEntry("002ivysaur", "base",
                new Entry("@all", null, 1.0F),
                new Entry("@special", Set.of(CardRarity.LEGENDARY), 1.0F)));

        this.values.put("venusaur", new CardEntry("003venusaur", "base",
                new Entry("@all", null, 1.0F),
                new Entry("@special", Set.of(CardRarity.LEGENDARY), 1.0F)));

        Map<String, Rational> pool = new LinkedHashMap<>();
        pool.put("bulbasaur", Rational.ONE);
        pool.put("ivysaur", Rational.ONE);
        pool.put("venusaur", Rational.ONE);
        this.pools.put("all", pool);
    }

}
