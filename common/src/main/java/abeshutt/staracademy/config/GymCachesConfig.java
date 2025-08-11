package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class GymCachesConfig extends FileConfig {

    @Expose private Map<String, Identifier> typeItems;

    @Override
    public String getPath() {
        return "gym_caches";
    }

    public Identifier getItemId(String type) {
        return this.typeItems.get(type);
    }

    @Override
    protected void reset() {
        this.typeItems = new HashMap<>();
        this.typeItems.put("normal", Registries.ITEM.getId(Items.WHITE_SHULKER_BOX));
    }

}
