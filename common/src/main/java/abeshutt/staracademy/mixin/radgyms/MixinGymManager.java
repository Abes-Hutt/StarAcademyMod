package abeshutt.staracademy.mixin.radgyms;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModConfigs;
import lol.gito.radgyms.RadGyms;
import lol.gito.radgyms.gym.GymManager;
import lol.gito.radgyms.gym.GymTemplate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cobblemon.mod.common.util.MiscUtilsKt.cobblemonResource;
import static net.minecraft.component.DataComponentTypes.CONTAINER;
import static net.minecraft.text.Text.translatable;

@Mixin(GymManager.class)
public class MixinGymManager {

    /**
     * @author StarAcademyMod
     * @reason Replace bundle caches with shulker boxes.
     */
    @Overwrite
    public final void handleLootDistribution(ServerPlayerEntity serverPlayer, GymTemplate template, int level, String type) {
        StarAcademyMod.LOGGER.debug("Handing loot distribution for {} at level {}", type, level);
        Identifier itemId = ModConfigs.GYM_CACHES.getItemId(type);
        StarAcademyMod.LOGGER.debug("Picked item {}", itemId);
        if(itemId == null) return;
        ItemStack item = new ItemStack(Registries.ITEM.get(itemId));
        List<ItemStack> loot = new ArrayList<>();
        StarAcademyMod.LOGGER.debug("Generating loot...");

        template.getLootTables().stream().filter(table -> {
            return level >= table.getLevels().getFirst() && level <= table.getLevels().getSecond();
        }).forEach(table -> {
            RadGyms.INSTANCE.debug("Settling level %d rewards for player %s after beating leader"
                    .formatted(level, serverPlayer.getName().getLiteralString()));

            LootTable registryLootTable = serverPlayer
                    .getServer()
                    .getReloadableRegistries()
                    .getRegistryManager()
                    .get(RegistryKeys.LOOT_TABLE)
                    .get(table.getId());

            if(registryLootTable == null) {
                return;
            }

            LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(serverPlayer.getServerWorld())
                    .add(LootContextParameters.THIS_ENTITY, serverPlayer)
                    .add(LootContextParameters.ORIGIN, serverPlayer.getPos())
                    .build(LootContextTypes.GIFT);

            loot.addAll(registryLootTable.generateLoot(lootContextParameterSet));
        });

        StarAcademyMod.LOGGER.debug("Generated loot {}", loot);

        if(item.contains(CONTAINER)) {
            item.set(CONTAINER, ContainerComponent.fromStacks(loot));
        }

        MutableText styledLevel = MutableText.of(Text.literal(level + "").getContent()).formatted(Formatting.GOLD);
        MutableText styledType = translatable(cobblemonResource("type." + type.toLowerCase()).toTranslationKey())
                .setStyle(
                        Style.EMPTY.withColor(Formatting.GREEN).withItalic(true)
                );

        item.set(
                DataComponentTypes.CUSTOM_NAME,
                translatable(
                        RadGyms.INSTANCE.modId("gym_reward").toTranslationKey("item"),
                        styledLevel, styledType
                )
        );

        StarAcademyMod.LOGGER.debug("Giving to player.");

        if(!serverPlayer.giveItemStack(item)) {
            StarAcademyMod.LOGGER.debug("Failed. Throwing on the floor instead.");

            serverPlayer.getWorld().spawnEntity(new ItemEntity(
                    serverPlayer.getWorld(),
                    serverPlayer.getPos().x,
                    serverPlayer.getPos().y,
                    serverPlayer.getPos().z,
                    item));
        }
    }

}
