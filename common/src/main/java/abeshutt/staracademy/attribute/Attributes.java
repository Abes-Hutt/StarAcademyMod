package abeshutt.staracademy.attribute;

import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.math.Rational;

public class Attributes {

    public static final AttributePath<Rational> SHINY_CHANCE = AttributePath.absolute("shiny_chance");

    public static AttributePath<Rational> ofBucketWeight(String name) {
        return AttributePath.absolute("bucket_weight", name);
    }

    public static AttributePath<Rational> ofLabelWeight(String name) {
        return AttributePath.absolute("type_weight", name);
    }

}
