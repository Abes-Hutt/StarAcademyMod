package abeshutt.staracademy.item.data;

import abeshutt.staracademy.attribute.Attribute;
import abeshutt.staracademy.attribute.NumberAttribute;

import java.util.Iterator;
import java.util.Stack;

public class RecursiveAttributeIterator implements Iterator<Attribute<?>> {

    private Attribute<?> root;
    private Stack<Integer> stack;

    private Iterator<Attribute<?>> children;

    public static void main(String[] args) {
        NumberAttribute root = new NumberAttribute();
        System.out.println(root);

        for(int i = 0; i < 3; i++) {
            NumberAttribute child = new NumberAttribute();
            System.out.println(child);

            for(int i1 = 0; i1 < 2; i1++) {
                NumberAttribute child2 = new NumberAttribute();
                child.getChildren().add(child2);
                System.out.println(child2);
            }

            root.getChildren().add(child);
        }

        System.out.println("==========================");
        Iterator<Attribute<?>> iter = new RecursiveAttributeIterator(root);

        while(iter.hasNext()) {
            System.out.println(iter.next());
        }
    }

    public RecursiveAttributeIterator(Attribute<?> root) {
        this.root = root;
        this.stack = new Stack<>();
        this.stack.push(0);
    }

    private void compute() {
        if(this.children == null) {
            this.children = this.root.getChildren().iterator();
            return;
        } else if(this.children.hasNext()) {
            return;
        }

        while(!this.stack.isEmpty()) {
            int index = this.stack.peek();

            if(index < this.root.getChildren().size()) {
                this.root = this.root.getChildren().get(index);
                this.stack.push(0);
                this.children = null;
                this.compute();
                return;
            } else {
                this.root = this.root.getParent();
                this.stack.pop();
            }
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
