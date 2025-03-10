package abeshutt.staracademy.entity;

import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModEntities;
import abeshutt.staracademy.init.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class DuelingGloveEntity extends ThrownItemEntity {

    public DuelingGloveEntity(EntityType<DuelingGloveEntity> type, World world) {
        super(type, world);
    }

    public DuelingGloveEntity(World world, LivingEntity thrower, ItemStack stack) {
        super(ModEntities.DUELING_GLOVE.get(), thrower, world);
        this.getDataTracker().set(ITEM, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.AIR;
    }

    protected void onEntityHit(EntityHitResult result) {
        super.onEntityHit(result);

        if(result.getEntity() instanceof LivingEntity entity) {
            entity.takeKnockback(1.5D, -this.getVelocity().x, -this.getVelocity().z);
            this.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundCategory.PLAYERS, 1.0F, 1.2F);
        }

        if(this.getOwner() instanceof ServerPlayerEntity thrower && result.getEntity() instanceof ServerPlayerEntity target) {
            for(ServerPlayerEntity player : thrower.getServer().getPlayerManager().getPlayerList()) {
                player.sendMessage(Text.empty()
                        .append(thrower.getName())
                        .append(Text.literal(" has challenged ").formatted(Formatting.GRAY))
                        .append(target.getName())
                        .append(Text.literal(" to a duel!").formatted(Formatting.GRAY)));
            }

            thrower.getItemCooldownManager().set(ModItems.DUELING_GLOVE.get(), ModConfigs.DUELING.getCooldownTicks());
        }
    }

    protected void onCollision(HitResult result) {
        super.onCollision(result);
        this.discard();
    }

}
