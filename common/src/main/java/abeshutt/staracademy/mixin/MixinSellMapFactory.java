package abeshutt.staracademy.mixin;

import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TradeOffers.SellMapFactory.class)
public class MixinSellMapFactory {

    @Redirect(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;locateStructure(Lnet/minecraft/registry/tag/TagKey;Lnet/minecraft/util/math/BlockPos;IZ)Lnet/minecraft/util/math/BlockPos;"))
    public BlockPos create(ServerWorld instance, TagKey<Structure> optional, BlockPos blockPos, int structureTag, boolean pos) {
        return null;
    }

}
