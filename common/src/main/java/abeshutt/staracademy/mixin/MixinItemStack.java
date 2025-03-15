package abeshutt.staracademy.mixin;

import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.util.ItemUseLogic;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static abeshutt.staracademy.util.ItemUseLogic.CommandExecutionContext.PLAYER;
import static abeshutt.staracademy.util.ItemUseLogic.CommandExecutionContext.SERVER;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow public abstract Item getItem();

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ci) {
        if(!user.getWorld().isClient() && user.getServer() != null) {
            ItemUseLogic logic = ModConfigs.ITEM_LOGIC.getUseLogic((ItemStack)(Object)this).orElse(null);
            if(logic == null) return;

            for(String command : logic.getCommands()) {
                command = command.replace("${user_uuid}", user.getUuid().toString())
                            .replace("${user_name}", user.getGameProfile().getName());

                if(logic.getContext() == PLAYER) {
                    user.getServer().getCommandManager().executeWithPrefix(user.getCommandSource(), command);
                } else if(logic.getContext() == SERVER) {
                    user.getServer().getCommandManager().executeWithPrefix(user.getServer().getCommandSource(), command);
                }
            }

            this.getItem().use(world, user, hand);

            if(logic.isConsumable() && !user.isCreative()) {
                user.getStackInHand(hand).decrement(1);
            }

            ci.setReturnValue(TypedActionResult.success(user.getStackInHand(hand)));
        }
    }

}
