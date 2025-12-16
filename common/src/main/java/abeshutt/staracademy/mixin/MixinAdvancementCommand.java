package abeshutt.staracademy.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.AdvancementCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementCommand.class)
public class MixinAdvancementCommand {

    @Inject(method = "executeAdvancement", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/exceptions/DynamicCommandExceptionType;create(Ljava/lang/Object;)Lcom/mojang/brigadier/exceptions/CommandSyntaxException;", shift = At.Shift.BEFORE), cancellable = true)
    private static void executeAdvancement(CallbackInfoReturnable<Integer> ci) throws CommandSyntaxException {
        ci.cancel();
    }

}
