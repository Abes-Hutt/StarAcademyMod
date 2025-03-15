package abeshutt.staracademy.item;

import abeshutt.staracademy.entity.SlingshotEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class SlingshotItem extends RangedWeaponItem {

    public SlingshotItem() {
        super(new Settings());
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if(user instanceof PlayerEntity player) {
            float progress = getPullProgress(this.getMaxUseTime(stack) - remainingUseTicks) / 2.0F;

            if(progress < 0.1D) {
                return;
            }

            if(!world.isClient) {
                SlingshotEntity entity = new SlingshotEntity(world, player, new ItemStack(Items.FIRE_CHARGE));
                entity.setVelocity(entity, player.getPitch(), player.getYaw(), 0.0F, progress, 1.0F);
                world.spawnEntity(entity);
            }

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
                    1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + progress * 0.5F);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }
    }

    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return ItemStack::isEmpty;
    }

    @Override
    public int getRange() {
        return 15;
    }

}
