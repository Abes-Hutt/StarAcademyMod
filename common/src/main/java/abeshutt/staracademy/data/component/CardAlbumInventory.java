package abeshutt.staracademy.data.component;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.basic.SerializableAdapter;
import abeshutt.staracademy.item.CardItem;
import abeshutt.staracademy.world.inventory.BaseInventory;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class CardAlbumInventory extends BaseInventory {

    public static final SerializableAdapter<CardAlbumInventory, NbtCompound, JsonObject> ADAPTER = Adapters.of(CardAlbumInventory::new, false);

    public CardAlbumInventory() {
        super(6 * 2);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if(!(stack.getItem() instanceof CardItem)) {
            return false;
        }

        return super.canInsert(stack);
    }

    public CardAlbumInventory copy() {
        CardAlbumInventory copy = new CardAlbumInventory();

        for(int i = 0; i < this.size(); i++) {
           copy.setStack(i, this.getStack(i));
        }

        return copy;
    }

}
