package abeshutt.staracademy.mixin.alphas;

import abeshutt.staracademy.world.random.JavaRandom;
import dev.cudzer.cobblemonalphas.CobblemonAlphasMod;
import dev.cudzer.cobblemonalphas.config.ModConfig;
import dev.cudzer.cobblemonalphas.data.AlphaJsonDataManager;
import dev.cudzer.cobblemonalphas.entity.Alpha;
import dev.cudzer.cobblemonalphas.entity.spawner.AlphaSpawner;
import dev.cudzer.cobblemonalphas.entity.spawner.spawnData.location.ISpawnLocation;
import dev.cudzer.cobblemonalphas.entity.spawner.spawnData.safety.ISpawnCondition;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AlphaSpawner.class)
public abstract class MixinAlphaSpawner {

    @Shadow private MinecraftServer server;
    @Shadow private ISpawnLocation spawnLocationSelector;
    @Shadow private List<ISpawnCondition> spawnConditions;

    @Shadow public abstract void spawnAlphaEntity(Alpha alpha, World level, Vec3i spawnPosition, boolean doHerdSpawning);

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    public void tick(CallbackInfo ci) {
        if(this.server.getTicks() % 20 == 0) {
            for(ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
                JavaRandom random = JavaRandom.ofNanoTime();

                if(random.nextFloat() >= ModConfig.alphaSpawnChance) {
                    continue;
                }

                this.academy$doSpawnAttempt(player);
            }
        }

        ci.cancel();
    }

    @Unique
    private void academy$doSpawnAttempt(ServerPlayerEntity player) {
        Alpha chosenAlpha = AlphaJsonDataManager.getRandomAlphaObj(this.server.getOverworld());
        int attemptedSpawns = 0;

        while(true) {
            ++attemptedSpawns;

            if(attemptedSpawns > ModConfig.spawnAttempts) {
                CobblemonAlphasMod.LOGGER.info("Maximum spawn attempts reached. Skipping this alpha spawn");
                return;
            }

            World world = player.getWorld();
            Vec3i spawnPos = this.spawnLocationSelector.getSpawnLocation(world, player.getPos());

            if(spawnPos != null) {
                BlockPos finalSpawnPos = new BlockPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

                if(this.spawnConditions.stream().allMatch(condition -> condition.isSafe(world, finalSpawnPos))) {
                    RegistryKey<Biome> biomeKey = world.getBiome(finalSpawnPos).getKey().orElse(null);
                    Alpha tempAlpha;

                    if(biomeKey != null) {
                        tempAlpha = AlphaJsonDataManager.getRandomAlphaForBiome(player.getWorld(),
                                biomeKey, !world.isSkyVisible(finalSpawnPos))
                                .values().stream().toList().getFirst();

                        if(tempAlpha != null) {
                            chosenAlpha = tempAlpha;
                        }
                    }

                    this.spawnAlphaEntity(chosenAlpha, world, spawnPos, ModConfig.doHerdSpawning);
                    String announcement = ModConfig.spawnAnnouncementMessage;

                    if(ModConfig.showCoordinatesInAnnouncement) {
                        announcement += " (" + spawnPos.getX() + ", " + spawnPos.getY() + ", " + spawnPos.getZ() + ")";
                    }

                    this.server.getPlayerManager().broadcast(Text.literal(announcement), false);
                    return;
                }
            }
        }
    }

}
