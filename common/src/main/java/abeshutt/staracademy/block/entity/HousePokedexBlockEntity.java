package abeshutt.staracademy.block.entity;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.init.ModBlocks;
import abeshutt.staracademy.util.ClientScheduler;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class HousePokedexBlockEntity extends BaseBlockEntity implements GeoAnimatable {

    protected UUID house;

    public double rotation;
    public double lastRotation;
    public double targetRotation;
    private final AnimatableInstanceCache animator;

    public HousePokedexBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.Entities.HOUSE_POKEDEX.get(), pos, state);
        this.animator = GeckoLibUtil.createInstanceCache(this);
    }

    public UUID getHouse() {
        return this.house;
    }

    public void setHouse(UUID house) {
        this.house = house;
        this.sendUpdatesToClient();
    }

    public void tick() {
        if(this.world == null || !this.world.isClient()) {
            return;
        }

        this.lastRotation = this.rotation;
        PlayerEntity player = this.world.getClosestPlayer((double)pos.getX() + 0.5,
                (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 3.0, false);

        if(player != null) {
            double d = player.getX() - ((double)pos.getX() + 0.5);
            double e = player.getZ() - ((double)pos.getZ() + 0.5);
            this.targetRotation = (float) MathHelper.atan2(e, d);
        } else {
            this.targetRotation += 0.02F;
        }

        while(this.rotation >= Math.PI) {
            this.rotation -= Math.PI * 2.0D;
        }

        while(this.rotation < -Math.PI) {
            this.rotation += Math.PI * 2.0D;
        }

        while(this.targetRotation >= Math.PI) {
            this.targetRotation -= Math.PI * 2.0D;
        }

        while(this.targetRotation < -Math.PI) {
            this.targetRotation += Math.PI * 2.0D;
        }

        double increment = this.targetRotation - this.rotation;

        while(increment >= Math.PI) {
            increment -= Math.PI * 2.0D;
        }

        while(increment < -Math.PI) {
            increment += Math.PI * 2.0D;
        }

        this.rotation += increment * 0.4F;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, UpdateType type) {
        Adapters.UUID.writeNbt(this.house).ifPresent(tag -> nbt.put("house", tag));
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, UpdateType type) {
        this.house = Adapters.UUID.readNbt(nbt.get("house")).orElse(null);
    }

    protected static final RawAnimation OPEN = RawAnimation.begin().thenPlayAndHold("open");
    protected static final RawAnimation CLOSE = RawAnimation.begin().thenPlayAndHold("closed");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registry) {
        registry.add(new AnimationController<>(this, "Main", 5, state -> {
            PlayerEntity player = this.world.getClosestPlayer((double)pos.getX() + 0.5,
                    (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 3.0, false);

            if(player != null) {
                return state.setAndContinue(OPEN);
            } else {
                return state.setAndContinue(CLOSE);
            }
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animator;
    }

    @Override
    public double getTick(Object object) {
        return ClientScheduler.getTick(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true));
    }

}
