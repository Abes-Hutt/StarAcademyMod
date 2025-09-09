package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.LinkedHashMap;
import java.util.Map;

public class StarterKitConfig extends FileConfig {

    @Expose private Map<EquipmentSlot, ItemStack> equipment;

    @Override
    public String getPath() {
        return "starter_kit";
    }

    public Map<EquipmentSlot, ItemStack> getEquipment() {
        return this.equipment;
    }

    @Override
    protected void reset() {
        this.equipment = new LinkedHashMap<>();

        for(EquipmentSlot slot : EquipmentSlot.values()) {
            this.equipment.put(slot, ItemStack.EMPTY);
        }

        this.equipment.put(EquipmentSlot.OFFHAND, new ItemStack(Items.GOLDEN_APPLE));
    }

}
