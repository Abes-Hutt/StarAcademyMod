package abeshutt.staracademy.mixin.ftbquests;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.util.ProxyNotifyItemRewardMessage;
import dev.ftb.mods.ftbquests.net.NotifyItemRewardMessage;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true, print = true)
@Mixin(targets = { "dev.ftb.mods.ftbquests.net.NotifyItemRewardMessage" })
public class MixinNotifyItemRewardMessage implements ProxyNotifyItemRewardMessage {

    @Shadow @Final @Mutable public static PacketCodec<RegistryByteBuf, NotifyItemRewardMessage> STREAM_CODEC;

    @Unique private long academy$id;

    @Override
    public long getId() {
        return this.academy$id;
    }

    @Override
    public void setId(long id) {
        this.academy$id = id;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void clinit(CallbackInfo ci) {
        STREAM_CODEC = PacketCodec.tuple(
                ItemStack.OPTIONAL_PACKET_CODEC, NotifyItemRewardMessage::stack,
                PacketCodecs.VAR_INT, NotifyItemRewardMessage::count,
                PacketCodecs.BOOL, NotifyItemRewardMessage::disableBlur,
                PacketCodecs.VAR_LONG, ProxyNotifyItemRewardMessage::getId,
                (stack, count, disableBlur, id) -> {
                    NotifyItemRewardMessage message = new NotifyItemRewardMessage(stack, count, disableBlur);
                    ProxyNotifyItemRewardMessage.setId(message, id);
                    return message;
                }
        );
    }

    @Inject(method = "lambda$handle$0", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/client/FTBQuestsNetClient;displayItemRewardToast(Lnet/minecraft/item/ItemStack;IZ)V", shift = At.Shift.BEFORE))
    private static void handle(NotifyItemRewardMessage message, CallbackInfo ci) {
        StarAcademyMod.QUEST_ID.set(ProxyNotifyItemRewardMessage.getId(message));
    }

}
