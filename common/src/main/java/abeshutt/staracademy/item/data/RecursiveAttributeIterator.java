package abeshutt.staracademy.item.data;

import abeshutt.staracademy.attribute.Attribute;
import abeshutt.staracademy.attribute.parent.ModifierAttributeParent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecursiveAttributeIterator implements Iterator<Attribute<?>> {

    private Attribute<?> root;
    private Iterator<Attribute<?>> children;

    public RecursiveAttributeIterator(Attribute<?> root) {
        this.root = root;
    }

    private void compute() {
        if(this.children == null) {
            this.children = this.root.getChildren().iterator();
        }

        while(!this.children.hasNext()) {
            if(this.root.getChildren().isEmpty()) {
                int index;

                do {
                    if(this.root.getParent() == null) {
                        return;
                    }

                    index = ((ModifierAttributeParent)this.root.getParent()).getIndex();
                    this.root = this.root.getParent().get();
                } while(index + 1 >= this.root.getChildren().size());

                List<Attribute<?>> children = new ArrayList<>(this.root.getChildren());
                this.root = children.get(index + 1);
            } else {
                this.root = this.root.getChildren().iterator().next();
            }

            this.children = this.root.getChildren().iterator();
        }
    }

    @Override
    public boolean hasNext() {
        this.compute();
        return this.children.hasNext();
    }

    @Override
    public Attribute<?> next() {
        this.compute();
        return this.children.next();
    }

}
