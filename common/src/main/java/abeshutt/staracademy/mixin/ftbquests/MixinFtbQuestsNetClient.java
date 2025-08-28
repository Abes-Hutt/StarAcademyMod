package abeshutt.staracademy.mixin.ftbquests;

import abeshutt.staracademy.StarAcademyMod;
import dev.architectury.hooks.item.ItemStackHooks;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.client.ClientUtils;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.FTBQuestsClientConfig;
import dev.ftb.mods.ftbquests.client.NotificationStyle;
import dev.ftb.mods.ftbquests.client.gui.IRewardListenerScreen;
import dev.ftb.mods.ftbquests.client.gui.RewardKey;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true, print = true)
@Mixin(targets = { "dev.ftb.mods.ftbquests.client.FTBQuestsNetClient" })
public class MixinFtbQuestsNetClient {

    @Redirect(method = "displayItemRewardToast", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/client/NotificationStyle;notifyReward(Lnet/minecraft/text/Text;Ldev/ftb/mods/ftblibrary/icon/Icon;)V"))
    private static void notifyReward(NotificationStyle instance, Text text, Icon icon) {
        QuestObjectBase base = ClientQuestFile.INSTANCE.getBase(StarAcademyMod.QUEST_ID.get());

        for(NotificationStyle style : NotificationStyle.values()) {
            if(base != null && base.hasTag("style_" + NotificationStyle.NAME_MAP.getName(style))) {
                style.notifyReward(text, icon);
                return;
            }
        }

        instance.notifyReward(text, icon);
    }

    @Inject(method = "displayRewardToast", at = @At("HEAD"), cancellable = true)
    private static void displayRewardToast(long id, Text text, Icon icon, boolean disableBlur, CallbackInfo ci) {
        QuestObjectBase base = ClientQuestFile.INSTANCE.getBase(id);

        for(NotificationStyle style : NotificationStyle.values()) {
            if(base != null && base.hasTag("style_" + NotificationStyle.NAME_MAP.getName(style))) {
                Icon actualIcon = icon.isEmpty() ? base.getIcon() : icon;

                if(!IRewardListenerScreen.add(new RewardKey(text.getString(), actualIcon, disableBlur), 1)) {
                    style.notifyReward(text, actualIcon);
                }

                ci.cancel();
                return;
            }
        }
    }

    @Redirect(method = "notifyPlayerOfCompletion", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/client/NotificationStyle;notifyCompletion(J)Z"), remap = false)
    private static boolean notifyPlayerOfCompletion(NotificationStyle instance, long id) {
        QuestObjectBase base = ClientQuestFile.INSTANCE.getBase(id);

        for(NotificationStyle style : NotificationStyle.values()) {
            if(base != null && base.hasTag("style_" + NotificationStyle.NAME_MAP.getName(style))) {
                return style.notifyCompletion(id);
            }
        }

        return instance.notifyCompletion(id);
    }

}
