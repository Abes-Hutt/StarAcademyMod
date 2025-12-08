package abeshutt.staracademy.config;

import abeshutt.staracademy.data.entity.EntityPredicate;
import com.google.gson.annotations.Expose;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.Map;

public class EntityYeeterConfig extends FileConfig {

    @Expose private Map<Identifier, EntityPredicate> blacklist;

    @Override
    public String getPath() {
        return "entity_yeeter";
    }

    public boolean contains(Entity entity) {
        if (entity == null || entity.getWorld() == null) {
            return false;
        }

        Identifier dimension = entity.getWorld().getRegistryKey().getValue();
        return this.blacklist != null && this.blacklist.getOrDefault(dimension, EntityPredicate.FALSE).test(entity);
    }

    @Override
    protected void reset() {
        this.blacklist = null;
    }

}
