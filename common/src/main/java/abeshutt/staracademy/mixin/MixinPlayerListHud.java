package abeshutt.staracademy.mixin;

import abeshutt.staracademy.world.data.AcademyHouse;
import abeshutt.staracademy.world.data.HouseData;
import abeshutt.staracademy.world.data.NickData;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    @Inject(method = "applyGameModeFormatting", at = @At(value = "RETURN"), cancellable = true)
    public void getPlayerName(PlayerListEntry entry, MutableText name, CallbackInfoReturnable<Text> ci) {
        if(ci.getReturnValue() instanceof MutableText result) {
            UUID uuid = entry.getProfile().getId();
            String nick = NickData.CLIENT.get(uuid).orElse(null);
            AcademyHouse house = HouseData.CLIENT.getFor(uuid).orElse(null);

            MutableText display = nick != null ? Text.literal(nick) : result;

            if(house != null) {
                display = display.setStyle(display.getStyle().withColor(house.getColor()));
            }

            ci.setReturnValue(display);
        }
    }

}
