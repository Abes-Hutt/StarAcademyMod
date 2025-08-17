package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.world.random.JavaRandom;
import abeshutt.staracademy.world.random.RandomSource;

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
