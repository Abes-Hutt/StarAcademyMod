package abeshutt.staracademy.mixin;

import abeshutt.staracademy.init.ModItems;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.world.data.LegendaryItemData;
import abeshutt.staracademy.world.random.JavaRandom;
import abeshutt.staracademy.world.random.RandomSource;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootTable.class)
public class MixinLootTable {

    @Inject(method = "generateLoot(Lnet/minecraft/loot/context/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;",
            at = @At(value = "RETURN"), cancellable = true)
    private void generateLoot(LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> ci) {
        MinecraftServer server = context.getWorld().getServer();
        ServerPlayerEntity player;
        BlockPos pos;
        Identifier dimension;

        if(context.get(LootContextParameters.THIS_ENTITY) instanceof ServerPlayerEntity thisEntity) {
            player = thisEntity;
        } else if(context.get(LootContextParameters.ATTACKING_ENTITY) instanceof ServerPlayerEntity attackingEntity) {
            player = attackingEntity;
        } else {
            player = null;
        }

        if(context.get(LootContextParameters.BLOCK_ENTITY) instanceof BlockEntity blockEntity) {
            pos = blockEntity.getPos();
        } else {
            pos = null;
        }

        dimension = context.getWorld().getRegistryKey().getValue();

        LegendaryItemData data = ModWorldData.LEGENDARY_ITEM.getGlobal(server);

        ObjectArrayList<ItemStack> result = ci.getReturnValue();
        int count = 0;

        for(ItemStack stack : result) {
           if(stack.getItem() == ModItems.LEGENDARY_PLACEHOLDER.get()) {
               count += stack.getCount();
           }
        }

        result.removeIf(stack -> stack.getItem() == ModItems.LEGENDARY_PLACEHOLDER.get());

        if(player != null) {
            for(int i = 0; i < count; i++) {
                data.getRemainingItem(JavaRandom.ofNanoTime()).ifPresent(item -> {
                    ItemStack stack = new ItemStack(item);
                    result.add(stack);
                    data.add(server, Registries.ITEM.getId(item), player.getUuid(), pos, dimension);
                });
            }
        }

        ci.setReturnValue(result);
    }

}
