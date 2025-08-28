package abeshutt.staracademy.mixin.ftbquests;

import abeshutt.staracademy.util.ProxyNotifyItemRewardMessage;
import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.ItemReward;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Debug(export = true, print = true)
@Mixin(targets = { "dev.ftb.mods.ftbquests.quest.reward.ItemReward" })
public abstract class MixinItemReward extends Reward {

    public MixinItemReward(long id, Quest q) {
        super(id, q);
    }

    @Redirect(method = "claim", at = @At(value = "INVOKE", target = "Ldev/architectury/networking/NetworkManager;sendToPlayer(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/packet/CustomPayload;)V"))
    private void claim(ServerPlayerEntity player, CustomPayload payload) {
        ProxyNotifyItemRewardMessage.setId(payload, this.getId());
        NetworkManager.sendToPlayer(player, payload);
    }

}
