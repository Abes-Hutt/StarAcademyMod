package abeshutt.staracademy.entity;

import abeshutt.staracademy.StarAcademyMod;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static com.cobblemon.mod.common.util.LocalizationUtilsKt.commandLang;

public class NurseNPCEntity extends HumanEntity {

    public NurseNPCEntity(EntityType<? extends PathAwareEntity> type, World world) {
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
    public void handleStatus(byte status) {
        if(status == 14) {
            for(int i = 0; i < 5; ++i) {
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 1.0, this.getParticleZ(1.0), d0, d1, d2);
            }
        }

        super.handleStatus(status);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity user, Hand hand) {
        if(user instanceof ServerPlayerEntity player) {
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            party.heal();
            player.sendMessage(commandLang("healpokemon.heal", player.getName()), true);
            this.getWorld().sendEntityStatus(this, (byte)14);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public Identifier getSkinTexture() {
        return StarAcademyMod.id("textures/entity/nurse_npc.png");
    }

}

