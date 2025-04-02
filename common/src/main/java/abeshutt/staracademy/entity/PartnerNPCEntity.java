package abeshutt.staracademy.entity;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.event.CommonEvents;
import abeshutt.staracademy.init.ModConfigs;
import com.cobblemon.mod.common.api.Priority;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PartnerNPCEntity extends HumanEntity {

    private final Map<UUID, PartnerNPCConversation> conversations;

    public PartnerNPCEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.conversations = new HashMap<>();
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

    public Optional<PartnerNPCConversation> getConversation(UUID uuid) {
        return Optional.ofNullable(this.conversations.get(uuid));
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
        this.setCustomNameVisible(false);

        if(!this.getWorld().isClient() && this.getServer() != null) {
            this.setCustomName(Text.literal(ModConfigs.NPC.getPartnerNPCName()));

            this.conversations.forEach((uuid, conversation) -> {
                ServerPlayerEntity player = this.getServer().getPlayerManager().getPlayer(uuid);

                if(player != null) {
                    conversation.onTick(player, this);
                }
            });
        }

        super.tick();
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if(player instanceof ServerPlayerEntity serverPlayer && hand == Hand.MAIN_HAND) {
            this.conversations.computeIfAbsent(player.getUuid(), PartnerNPCConversation::new)
                    .onInteract(serverPlayer, this);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public Identifier getSkinTexture() {
        return StarAcademyMod.id("textures/entity/partner_npc.png");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        NbtCompound conversations = new NbtCompound();

        this.conversations.forEach((uuid, conversation) -> {
            conversation.writeNbt().ifPresent(tag -> conversations.put(uuid.toString(), tag));
        });
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        this.conversations.clear();
        NbtCompound conversations = nbt.getCompound("conversations");

        for(String key : conversations.getKeys()) {
            UUID uuid = UUID.fromString(key);
            PartnerNPCConversation conversation = new PartnerNPCConversation(uuid);
            conversation.readNbt(nbt.getCompound(key));
            this.conversations.put(uuid, conversation);
        }
    }

    static {
        CommonEvents.POKEMON_SENT_POST.register(event -> {
            World world = event.getPokemonEntity().getWorld();
            if(world.isClient() || world.getServer() == null) return;
            UUID owner = event.getPokemonEntity().getOwnerUuid();
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(owner);
            if(player == null) return;

            Box area = event.getPokemonEntity().getBoundingBox().expand(48.0D);

            world.getEntitiesByClass(PartnerNPCEntity.class, area, npc -> {
                PartnerNPCConversation convo = npc.conversations.get(owner);

                if(convo != null) {
                    convo.onPokemonSent(player, npc, event);
                }

                return false;
            });
        }, Priority.NORMAL);
    }

}
