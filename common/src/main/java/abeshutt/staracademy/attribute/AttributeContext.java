package abeshutt.staracademy.attribute;

public class AttributeContext {

    private Attribute<?> root;

    public AttributeContext() {

    }

    public Attribute<?> getRoot() {
        return this.root;
    }

    public void setRoot(Attribute<?> root) {
        this.root = root;
    }

}
