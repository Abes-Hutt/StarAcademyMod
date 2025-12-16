package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityYeeterConfig extends FileConfig {

    @Expose private Map<Identifier, Set<Identifier>> blacklist;

    @Override
    public String getPath() {
        return "entity_yeeter";
    }

    public boolean contains(Entity entity) {
        if (entity == null || entity.getWorld() == null) {
            return false;
        }

        Identifier dimension = entity.getWorld().getRegistryKey().getValue();
        return this.blacklist != null && this.blacklist.getOrDefault(dimension, new HashSet<>())
                .contains(Registries.ENTITY_TYPE.getId(entity.getType()));
    }

    @Override
    protected void reset() {
        this.blacklist = null;
    }

}
