package abeshutt.staracademy.attribute.modifier;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.attribute.type.NumberAttributeType;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.world.random.RandomSource;
import abeshutt.staracademy.world.roll.NumberRoll;
import com.google.gson.JsonObject;

import java.util.Optional;
import java.util.function.BinaryOperator;

public class OperandNumberAttribute extends ConfigAttribute<Rational, OperandNumberAttribute.Config> {

    private final BinaryOperator<Rational> operation;
    private Rational operand;

    protected OperandNumberAttribute(Config config, BinaryOperator<Rational> operation) {
        super(new NumberAttributeType(), config);
        this.operation = operation;
    }

    public Option<Rational> getOperand() {
        if(this.operand != null) {
            return super.get(this.operand);
        } else if(this.getConfig().indirect != null) {
            return super.get(this.path(this.getConfig().indirect).get());
        }

        return Option.absent();
    }

    @Override
    public Option<Rational> get(Option<Rational> value) {
        Option<Rational> operand = this.getOperand();
        if(value.isAbsent()) return operand;
        if(operand.isAbsent()) return value;
        return Option.present(this.operation.apply(value.get(), operand.get()));
    }

    @Override
    public void generate(RandomSource random) {
        if(this.getConfig().direct != null) {
            this.operand = this.getConfig().direct.get(random);
        } else {
            this.operand = null;
        }
    }

    public static class Config extends ConfigAttribute.Config {
        private NumberRoll direct;
        private AttributePath<Rational> indirect;

        public Config() {

        }

        public Config(NumberRoll direct, AttributePath<Rational> indirect) {
            this.direct = direct;
            this.indirect = indirect;
        }

        @Override
        public Optional<JsonObject> writeJson() {
            return super.writeJson().map(json -> {
                return json;
            });
        }

        @Override
        public void readJson(JsonObject json) {

        }
    }

}
