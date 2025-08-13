package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.world.random.RandomSource;

public class AttributeContext {

    private final RandomSource random;

    public AttributeContext(RandomSource random) {
        this.random = random;
    }

    public RandomSource getRandom() {
        return this.random;
    }

}
