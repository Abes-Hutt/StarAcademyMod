package abeshutt.staracademy.config;

import abeshutt.staracademy.data.item.ItemPredicate;
import abeshutt.staracademy.init.ModItems;
import com.google.gson.annotations.Expose;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StarBadgeConfig extends FileConfig {

    @Expose private boolean enabled;
    @Expose private List<ItemStack> startItems;
    @Expose private List<ItemPredicate> validItems;

    @Override
    public String getPath() {
        return "star_badge";
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public List<ItemStack> getStartItems() {
        return this.startItems;
    }

    public boolean isValid(ItemStack stack) {
        for(ItemPredicate predicate : this.validItems) {
            if(predicate.test(stack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void reset() {
        this.enabled = false;
        this.startItems = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
           this.startItems.add(new ItemStack(ModItems.STAR_BADGE.get()));
        }

        this.validItems = new ArrayList<>();
        this.validItems.add(ItemPredicate.of("academy:star_badge", true).orElseThrow());
    }

}
