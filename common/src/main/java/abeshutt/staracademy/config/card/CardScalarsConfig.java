package abeshutt.staracademy.config.card;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.WeightedList;
import abeshutt.staracademy.config.FileConfig;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.world.random.RandomSource;
import com.google.gson.annotations.Expose;

import java.util.*;

public class CardScalarsConfig extends FileConfig {

    @Expose private List<Rational> weights;
    @Expose private Map<String, List<Rational>> values;
    @Expose private Map<String, Map<String, Rational>> pools;

    @Override
    public String getPath() {
        return "card.scalars";
    }

    public int getGrade(RandomSource random) {
        WeightedList<Integer> weighted = WeightedList.empty();

        for(int i = 0; i < this.weights.size(); i++) {
            Rational weight = this.weights.get(i);
            weighted.add(i + 1, weight);
        }

        return weighted.getRandom(random).orElse(0);
    }

    public Optional<List<Rational>> get(String id) {
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
        this.weights = new ArrayList<>();
        this.values = new LinkedHashMap<>();
        this.pools = new LinkedHashMap<>();

        for(int i = 0; i < 10; i++) {
           this.weights.add(Rational.ONE);
        }

        this.values.put("base", List.of(
                Rational.of(2, 10),
                Rational.of(4, 10),
                Rational.of(6, 10),
                Rational.of(8, 10),
                Rational.of(10, 10),
                Rational.of(12, 10),
                Rational.of(14, 10),
                Rational.of(16, 10),
                Rational.of(18, 10),
                Rational.of(20, 10)));
    }
    
}
