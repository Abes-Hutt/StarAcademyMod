package abeshutt.staracademy.attribute;

import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.proxy.AttributeHolder;
import com.cobblemon.mod.common.api.spawning.SpawnBucket;
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence;
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition;
import com.cobblemon.mod.common.api.spawning.position.calculators.SpawnablePositionCalculator;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
    public boolean affectSpawnable(SpawnDetail detail, SpawnablePosition spawnablePosition) {
        return true;
    }

    @Override
    public float affectWeight(SpawnDetail detail, SpawnablePosition spawnablePosition, float weight) {
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
    public void affectSpawn(SpawnAction<?> action, Entity entity) {

    }

    @Override
    public void affectBucketWeights(@NotNull Map<SpawnBucket, Float> bucketWeights) {
        for (SpawnBucket bucket : bucketWeights.keySet()) {
            float weight = bucketWeights.get(bucket);
            float newWeight = AttributeHolder.getRoot(this.player).path(ofBucketWeight(bucket.getName())).map(attribute -> {
                Option<Rational> result = attribute.get(Option.present(Rational.of(weight)), AttributeContext.random());
                return result.isPresent() ? result.get().floatValue() : weight;
            }).orElse(weight);
            bucketWeights.put(bucket, newWeight);
        }
    }

    @Override
    public boolean isAllowedPosition(@NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull SpawnablePositionCalculator<?, ?> spawnablePositionCalculator) {
        return true;
    }

}
