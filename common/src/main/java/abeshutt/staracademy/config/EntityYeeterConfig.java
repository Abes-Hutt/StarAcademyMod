package abeshutt.staracademy.config;

import abeshutt.staracademy.data.entity.EntityPredicate;
import com.google.gson.annotations.Expose;
import net.minecraft.entity.Entity;

public class EntityYeeterConfig extends FileConfig {

    @Expose private EntityPredicate blacklist;

    @Override
    public String getPath() {
        return "entity_yeeter";
    }

    public boolean contains(Entity entity) {
        return this.blacklist != null && this.blacklist.test(entity);
    }

    @Override
    protected void reset() {
        this.blacklist = null;
    }

}
