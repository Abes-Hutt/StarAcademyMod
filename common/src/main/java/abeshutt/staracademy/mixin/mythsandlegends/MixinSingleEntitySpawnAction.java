package abeshutt.staracademy.mixin.mythsandlegends;

import com.bawnorton.mixinsquared.TargetHandler;
import com.cobblemon.mod.common.api.spawning.condition.AppendageCondition;
import com.cobblemon.mod.common.api.spawning.detail.SingleEntitySpawnAction;
import com.github.d0ctorleon.mythsandlegends.MythsAndLegends;
import com.github.d0ctorleon.mythsandlegends.cobblemon.spawning.condition.MythsAndLegendsConditions;
import com.github.d0ctorleon.mythsandlegends.cobblemon.spawning.condition.items.ItemCondition;
import com.github.d0ctorleon.mythsandlegends.cobblemon.spawning.condition.keyitem.KeyItemConditions;
import com.github.d0ctorleon.mythsandlegends.cobblemon.spawning.condition.keyitem.custom.CustomKeyItemCondition;
import com.github.d0ctorleon.mythsandlegends.cobblemon.spawning.condition.keyitem.custom.CustomSecondaryItemCondition;
import com.github.d0ctorleon.mythsandlegends.configs.ModConfigs;
import com.github.d0ctorleon.mythsandlegends.utils.DebtUtils;
import com.github.d0ctorleon.mythsandlegends.utils.InventoryUtils;
import com.github.d0ctorleon.mythsandlegends.utils.PlayerDataUtils;
import dev.architectury.platform.Platform;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Debug(export = true)
@Mixin(value = SingleEntitySpawnAction.class, priority = 1500)
public class MixinSingleEntitySpawnAction {

    @TargetHandler(mixin = "com.github.d0ctorleon.mythsandlegends.cobblemon.mixins.SingleEntitySpawnActionMixin", name = "MythsAndLegends$processConditions")
    @Inject(method = "@MixinSquared:Handler", at = @At(value = "HEAD"), cancellable = true)
    private void processConditions(List<AppendageCondition> appendages, ServerPlayerEntity player,
                                   boolean itemConditionMet, CallbackInfo ci) {
        ModConfigs config = MythsAndLegends.getConfigManager().getConfig();
        MythsAndLegends.getLogger().debug("SingleEntitySpawnActionMixin processConditions-Started");
        boolean configConsume = config.consume_items_on_successful_spawn;
        boolean consumeOnlyItems = config.ignore_key_items;

        for(AppendageCondition appendage : appendages) {
            if(appendage instanceof CustomKeyItemCondition condition) {
                if(condition.getItemConditions() == null) {
                    continue;
                }

                for(CustomKeyItemCondition.CustomConditionItem entry : condition.getItemConditions()) {
                    if(entry.consume || (configConsume && (!consumeOnlyItems || !itemConditionMet))) {
                        this.academy$removeCustomItemFromPlayer(player, entry.id, entry.count, config);
                    }
                }
            } else if(appendage instanceof CustomSecondaryItemCondition condition) {
                CustomKeyItemCondition.CustomConditionItem item = condition.getItemConditionIdentifier();
                if(item != null && (configConsume || item.consume)) {
                    this.academy$removeCustomItemFromPlayer(player, item.id, item.count, config);
                }
            } else if(appendage instanceof KeyItemConditions.KeyItem condition) {
                if(configConsume && condition.getItemCondtionIdentifier() != null && (!itemConditionMet || !config.ignore_key_items)) {
                    this.academy$removeKeyItemFromPlayer(player, condition.getItemCondtionIdentifier(), config);
                }
            } else if(appendage instanceof ItemCondition condition) {
                if(configConsume && condition.getItemConditionIdentifier() != null) {
                    this.academy$removeItemFromPlayer(player, condition.getItemConditionIdentifier(),
                            condition.getItemNamespace(), condition.getItemPath(), config);
                }
            }
        }

        ci.cancel();
    }

    @Unique
    private void academy$removeItemFromPlayer(ServerPlayerEntity player, Identifier id,
                                              String namespace, String path, ModConfigs config) {
        if(id != null) {
            Identifier ItemIdentifier = Identifier.of(namespace, path);
            Item item = player.getRegistryManager().get(RegistryKeys.ITEM).get(ItemIdentifier);
            int requiredCount = MythsAndLegendsConditions.parseRequiredCount(id.getPath());

            if(!InventoryUtils.removeItemFromInventory(player, item, requiredCount,
                    config.inventory_check_bundles,
                    config.inventory_check_shulker_boxes)) {
                DebtUtils debtUtils = new DebtUtils(Platform.getConfigFolder().resolve("MythsAndLegends"));
                debtUtils.addDebt(player.getUuid(), ItemIdentifier, requiredCount);
            }

            PlayerDataUtils.checkPlayerInventory(player);
        }
    }

    @Unique
    private void academy$removeCustomItemFromPlayer(ServerPlayerEntity player, Identifier customItemId, int count, ModConfigs config) {
        Item item = player.getRegistryManager().get(RegistryKeys.ITEM).get(customItemId);

        if(!InventoryUtils.removeItemFromInventory(player, item, count, config.inventory_check_bundles,
                config.inventory_check_shulker_boxes)) {
            DebtUtils debtUtils = new DebtUtils(Platform.getConfigFolder().resolve("MythsAndLegends"));
            debtUtils.addDebt(player.getUuid(), customItemId, count);
        }

        PlayerDataUtils.checkPlayerInventory(player);
    }

    @Unique
    private void academy$removeKeyItemFromPlayer(ServerPlayerEntity player, Identifier keyItem, ModConfigs config) {
        Item item = player.getRegistryManager().get(RegistryKeys.ITEM).get(keyItem);

        if(!InventoryUtils.removeItemFromInventory(player, item, 1, config.inventory_check_bundles,
                config.inventory_check_shulker_boxes)) {
            DebtUtils debtUtils = new DebtUtils(Platform.getConfigFolder().resolve("MythsAndLegends"));
            debtUtils.addDebt(player.getUuid(), keyItem, 1);
        }

        PlayerDataUtils.checkPlayerInventory(player);
    }

}