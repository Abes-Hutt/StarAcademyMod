package abeshutt.staracademy.attribute;

import abeshutt.staracademy.math.random.JavaRandom;
import abeshutt.staracademy.math.random.RandomSource;

public class AttributeContext {

    private final RandomSource random;

    public AttributeContext(RandomSource random) {
        this.random = random;
    }

    public static AttributeContext random() {
        return new AttributeContext(JavaRandom.ofNanoTime());
    }

    public RandomSource getRandom() {
        return this.random;
    }

}
