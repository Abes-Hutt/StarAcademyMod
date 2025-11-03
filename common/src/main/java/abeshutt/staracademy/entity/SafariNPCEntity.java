package abeshutt.staracademy.entity;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.world.data.save.SafariData;
import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.currency.CurrencyComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SafariNPCEntity extends HumanEntity {

    public SafariNPCEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F, 0.1F));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        this.setInvulnerable(true);
        super.tick();
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if(!player.getWorld().isClient() && hand == Hand.MAIN_HAND) {
            SafariData data = ModWorldData.SAFARI.getGlobal(player.getWorld());
            SafariData.Entry entry = data.getOrCreate(player.getUuid());

            if(!entry.isUnlocked()) {
                CurrencyComponent purse = ModComponents.CURRENCY.get(player);

                if(purse.getValue() < ModConfigs.NPC.getGradingCurrencyCost()) {
                    if(!entry.isPrompted()) {
                        player.sendMessage(Text.empty()
                                .append(Text.translatable("text.academy.safari.initial_locked").formatted(Formatting.GRAY)));
                        entry.setPrompted(true);
                        data.markDirty();
                    } else {
                        player.sendMessage(Text.empty()
                                .append(Text.translatable("text.academy.safari.unlock_broke").formatted(Formatting.GRAY)));
                    }
                } else {
                    purse.pushTransaction(-ModConfigs.NPC.getGradingCurrencyCost());
                    purse.commitTransactions();
                    player.sendMessage(Text.empty().append(Text.translatable("text.academy.safari.unlock_complete")
                            .formatted(Formatting.GRAY)));
                    entry.setUnlocked(true);
                    entry.setPrompted(true);
                    data.markDirty();
                }

                return ActionResult.SUCCESS;
            }

            if(data.isPaused()) {
                player.sendMessage(Text.empty()
                        .append(Text.translatable("text.academy.safari.initial_closed").formatted(Formatting.GRAY)));
            } else {
                player.sendMessage(Text.empty()
                        .append(Text.translatable("text.academy.safari.initial_open").formatted(Formatting.GRAY)));
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public Identifier getSkinTexture() {
        return StarAcademyMod.id("textures/entity/safari_npc.png");
    }

}

