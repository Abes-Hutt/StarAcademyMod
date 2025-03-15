package abeshutt.staracademy.config;

import abeshutt.staracademy.data.entity.EntityPredicate;
import abeshutt.staracademy.data.entity.PartialEntity;
import abeshutt.staracademy.data.nbt.PartialCompoundNbt;
import com.google.gson.annotations.Expose;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class EntityGroupsConfig extends FileConfig {

    @Expose private Map<Identifier, Set<EntityPredicate>> groups;

    @Override
    public String getPath() {
        return "entity_groups";
    }

    public boolean isInGroup(Identifier groupId, Vec3d pos, BlockPos blockPos, PartialCompoundNbt nbt) {
        for(EntityPredicate predicate : this.groups.getOrDefault(groupId, new HashSet<>())) {
            if(predicate.test(pos, blockPos, nbt)) {
                return true;
            }
        }

        return false;
    }

    public boolean isInGroup(Identifier groupId, Entity entity) {
        return this.isInGroup(groupId, entity.getPos(), entity.getBlockPos(), PartialCompoundNbt.of(entity.writeNbt(new NbtCompound())));
    }

    public boolean isInGroup(Identifier groupId, PartialEntity entity) {
        return this.isInGroup(groupId, entity.getPos(), entity.getBlockPos(), entity.getNbt());
    }

    @Override
    protected void reset() {
        this.groups = new LinkedHashMap<>();
    }

}
