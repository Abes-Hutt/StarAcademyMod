package abeshutt.staracademy.attribute.modifier;

import abeshutt.staracademy.attribute.Attribute;
import abeshutt.staracademy.attribute.type.AttributeType;
import com.google.gson.JsonElement;

public class JsonLazyAttribute extends LazyAttribute<JsonElement> {

    protected JsonLazyAttribute(JsonElement json) {
        super(json);
    }

    @Override
    protected JsonElement encode(AttributeType<?> type, Attribute<?> attribute) {
        return (JsonElement)type.getModifiers().writeJson((Attribute)attribute).orElse(null);
    }

    @Override
    protected Attribute<?> decode(AttributeType<?> type, JsonElement json) {
        return type.getModifiers().readJson(json).orElse(null);
    }

}
