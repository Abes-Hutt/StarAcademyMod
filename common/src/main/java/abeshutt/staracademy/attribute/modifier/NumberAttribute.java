package abeshutt.staracademy.attribute.modifier;

public class NumberAttribute {

    //public static Attribute<Rational> assign(Number value) {
     //   return new Assign(constant(Rational.of(value)));
    //}

    //public static Attribute<Rational> assign(AttributePath<Rational> attribute) {
    //    return new Assign(attribute(attribute));
    //}

    //public static Supplier<Attribute<T>> add(int value) {
    //    return new Add(constant(value));
    //}

    //public static Attribute<Rational> add(AttributePath<Integer> attribute) {
    //    return new Add(attribute(attribute));
    //}

    /*
    public static class Assign extends UnaryAttribute.Projection<Rational, Rational> {
        protected Assign(Argument<Rational> argument) {
            //super(NumberAttributeType.INSTANCE, Function.identity(),
            //        define("value", Adapters.RATIONAL));
            this.set(0, argument);
        }
    }

    public static class Add extends UnaryAttribute.Arithmetic<Rational, Rational> {
        protected Add(Argument<Integer> argument) {
            super(NumberAttributeType.INSTANCE, Rational::add,
                    define("value", Adapters.RATIONAL));
            this.set(0, argument);
        }
    }*/

}
