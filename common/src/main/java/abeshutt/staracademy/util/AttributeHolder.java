package abeshutt.staracademy.util;

import abeshutt.staracademy.attribute.again.Attribute;

public interface AttributeHolder {

    Attribute<?> getRoot();

    void setRoot(Attribute<?> root);

    static Attribute<?> getRoot(Object object) {
        return ((AttributeHolder)object).getRoot();
    }

    static void setRoot(Object object, Attribute<?> root) {
        ((AttributeHolder)object).setRoot(root);
    }

}
