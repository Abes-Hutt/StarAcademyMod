package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.util.AttributeHolder;
import com.cobblemon.mod.common.api.spawning.SpawnBucket;
import com.cobblemon.mod.common.api.spawning.context.SpawningContext;
import com.cobblemon.mod.common.api.spawning.context.calculators.SpawningContextCalculator;
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import static abeshutt.staracademy.attribute.Attributes.ofBucketWeight;
import static abeshutt.staracademy.attribute.Attributes.ofLabelWeight;

public class AttributePlayerInfluence implements SpawningInfluence {

    private final ServerPlayerEntity player;

    public AttributePlayerInfluence(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public boolean affectSpawnable(@NotNull SpawnDetail spawnDetail, @NotNull SpawningContext spawningContext) {
        return true;
    }

    @Override
    public float affectWeight(SpawnDetail detail, SpawningContext context, float weight) {
        for(String label : detail.getLabels()) {
            float value = weight;

            weight = AttributeHolder.getRoot(this.player).path(ofLabelWeight(label))
                .map(attribute -> {
                    Option<Rational> result = attribute.get(Option.present(Rational.of(value)), AttributeContext.random());
                    return result.isPresent() ? result.get().floatValue() : value;
                }).orElse(weight);
        }

        return weight;
    }

    @Override
    public void affectAction(SpawnAction<?> action) {

    }

    @Override
    public void affectSpawn(Entity entity) {

    }

    @Override
    public float affectBucketWeight(SpawnBucket bucket, float weight) {
        return AttributeHolder.getRoot(this.player).path(ofBucketWeight(bucket.getName())).map(attribute -> {
            Option<Rational> result = attribute.get(Option.present(Rational.of(weight)), AttributeContext.random());
            return result.isPresent() ? result.get().floatValue() : weight;
        }).orElse(weight);
    }

    @Override
    public boolean isAllowedPosition(ServerWorld world, BlockPos pos, SpawningContextCalculator<?, ?> calculator) {
        return true;
    }

}
