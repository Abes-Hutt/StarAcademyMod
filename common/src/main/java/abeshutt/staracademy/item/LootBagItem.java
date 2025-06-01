package abeshutt.staracademy.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class LootBagItem extends Item {

    private final int color;

    public LootBagItem(int color, Settings settings) {
        super(settings);
        this.color = color;
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy().setStyle(Style.EMPTY.withColor(this.color));
    }

}
