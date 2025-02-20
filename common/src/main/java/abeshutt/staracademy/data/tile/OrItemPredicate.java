package abeshutt.staracademy.data.tile;

import abeshutt.staracademy.data.item.ItemPredicate;
import abeshutt.staracademy.data.item.PartialItem;
import abeshutt.staracademy.data.nbt.PartialCompoundNbt;

import java.util.Arrays;

public class OrItemPredicate implements ItemPredicate {

    private ItemPredicate[] children;

    public OrItemPredicate(ItemPredicate... children) {
        this.children = children;
    }

    public ItemPredicate[] getChildren() {
        return this.children;
    }

    @Override
    public boolean test(PartialItem item, PartialCompoundNbt nbt) {
        for(ItemPredicate child : this.children) {
            if(child.test(item, nbt)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void validate(String path) {
        for(int i = 0; i < this.children.length; i++) {
           this.children[i].validate(path + "[" + i + "]");
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(this.children);
    }

}
