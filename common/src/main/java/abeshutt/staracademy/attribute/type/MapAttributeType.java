package abeshutt.staracademy.attribute.type;

import java.util.Map;

public abstract class MapAttributeType<K, V> extends AttributeType<Map<K, V>> {

    private final AttributeType<K> key;
    private final AttributeType<V> value;

    public MapAttributeType(AttributeType<K> key, AttributeType<V> value) {
        this.key = key;
        this.value = value;
    }

    public AttributeType<K> getKey() {
        return this.key;
    }

    public AttributeType<V> getValue() {
        return this.value;
    }

}
