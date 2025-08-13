package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.type.AttributeType;
import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.IAdapter;

public class ReferenceAttribute<T> extends ValueAttribute<T, AttributePath<T>, AttributePath<T>> {

    protected ReferenceAttribute(AttributeType<T> type) {
        super(type, null, null, (IAdapter)Adapters.ATTRIBUTE_PATH, (IAdapter)Adapters.ATTRIBUTE_PATH);
    }

    @Override
    protected Option<T> compute(Option<T> value, AttributeContext context) {
        return this.value == null ? Option.absent() : this.path(this.value).get(value, context);
    }

    @Override
    protected void generate(AttributeContext context) {
        this.value = this.getConfig();
    }

}
