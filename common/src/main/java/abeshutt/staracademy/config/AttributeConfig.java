package abeshutt.staracademy.config;

import abeshutt.staracademy.attribute.again.Attribute;
import com.google.gson.annotations.Expose;

import java.util.Map;

public class AttributeConfig extends FileConfig {

    @Expose protected Map<String, String> associations;
    @Expose protected Attribute<?> root;

    @Override
    public String getPath() {
        return "attribute";
    }

    @Override
    protected void reset() {

    }

}
