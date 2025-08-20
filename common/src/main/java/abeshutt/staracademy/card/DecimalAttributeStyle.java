package abeshutt.staracademy.card;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.math.Rational;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static abeshutt.staracademy.data.adapter.basic.EnumAdapter.Mode.NAME;

public class DecimalAttributeStyle extends AttributeStyle<Rational> {

    private int scale;
    private RoundingMode rounding;
    private boolean stripped;

    protected DecimalAttributeStyle() {

    }

    public DecimalAttributeStyle(int scale, RoundingMode rounding, boolean stripped) {
        this.scale = scale;
        this.rounding = rounding;
        this.stripped = stripped;
    }

    @Override
    public String format(Rational rational) {
        BigDecimal rounded = rational.toBigDecimal(this.scale, this.rounding);
        return this.stripped ? rounded.stripTrailingZeros().toPlainString() : rounded.toPlainString();
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            Adapters.INT.writeJson(this.scale).ifPresent(tag -> {
                json.add("scale", tag);
            });

            Adapters.ofEnum(RoundingMode.class, NAME).writeJson(this.rounding).ifPresent(tag -> {
                json.add("rounding", tag);
            });

            Adapters.BOOLEAN.writeJson(this.stripped).ifPresent(tag -> {
                json.add("stripped", tag);
            });

            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.scale = Adapters.INT.readJson(json.get("scale")).orElseThrow();
        this.rounding = Adapters.ofEnum(RoundingMode.class, NAME).readJson(json.get("rounding")).orElseThrow();
        this.stripped = Adapters.BOOLEAN.readJson(json.get("stripped")).orElseThrow();
    }

}
