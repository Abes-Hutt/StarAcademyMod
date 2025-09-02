package abeshutt.staracademy.mixin.radgyms;

import lol.gito.radgyms.RadGyms;
import lol.gito.radgyms.nbt.EntityDataSaver;
import lol.gito.radgyms.nbt.GymsNbtData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(targets = { "lol.gito.radgyms.world.PlayerSpawnHelper" })
public class MixinPlayerSpawnHelper {

    /**
     * @author Academy
     * @reason Start gym spawns at (0, 0).
     */
    @Overwrite
    public final BlockPos getUniquePlayerCoords(ServerPlayerEntity serverPlayer, ServerWorld serverWorld) {
        Random random = new Random(serverPlayer.getUuid().getMostSignificantBits());
        int playerX = random.nextInt(20000000);
        int playerZ = GymsNbtData.INSTANCE.incrementVisitCount((EntityDataSaver)serverPlayer) * 128;
        RadGyms.INSTANCE.debug("Derived player ${serverPlayer.name} unique X coordinate from UUID: $playerX");
        RadGyms.INSTANCE.debug("Derived player ${serverPlayer.name} unique Z coordinate from UUID: ${border.boundWest.toLong() + playerZ}");
        return new BlockPos(playerX, 0, playerZ);
    }

}
