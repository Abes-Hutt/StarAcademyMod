package abeshutt.staracademy.block.entity;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.UUID;

public class HousePokedexBlockEntity extends BaseBlockEntity {

    protected UUID house;

    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float flipRandom;
    public float flipTurn;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public float bookRotation;
    public float lastBookRotation;
    public float targetBookRotation;
    private static final Random RANDOM = Random.create();

    public HousePokedexBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.Entities.HOUSE_POKEDEX.get(), pos, state);
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

        this.pageTurningSpeed = this.nextPageTurningSpeed;
        this.lastBookRotation = this.bookRotation;
        PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 3.0, false);
        if (playerEntity != null) {
            double d = playerEntity.getX() - ((double)pos.getX() + 0.5);
            double e = playerEntity.getZ() - ((double)pos.getZ() + 0.5);
            this.targetBookRotation = (float) MathHelper.atan2(e, d);
            this.nextPageTurningSpeed += 0.1F;
            if (this.nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
                float f = this.flipRandom;

                do {
                    this.flipRandom += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
                } while(f == this.flipRandom);
            }
        } else {
            this.targetBookRotation += 0.02F;
            this.nextPageTurningSpeed -= 0.1F;
        }

        while(this.bookRotation >= 3.1415927F) {
            this.bookRotation -= 6.2831855F;
        }

        while(this.bookRotation < -3.1415927F) {
            this.bookRotation += 6.2831855F;
        }

        while(this.targetBookRotation >= 3.1415927F) {
            this.targetBookRotation -= 6.2831855F;
        }

        while(this.targetBookRotation < -3.1415927F) {
            this.targetBookRotation += 6.2831855F;
        }

        float g;
        for(g = this.targetBookRotation - this.bookRotation; g >= 3.1415927F; g -= 6.2831855F) {
        }

        while(g < -3.1415927F) {
            g += 6.2831855F;
        }

        this.bookRotation += g * 0.4F;
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
        ++this.ticks;
        this.pageAngle = this.nextPageAngle;
        float h = (this.flipRandom - this.nextPageAngle) * 0.4F;
        float i = 0.2F;
        h = MathHelper.clamp(h, -0.2F, 0.2F);
        this.flipTurn += (h - this.flipTurn) * 0.9F;
        this.nextPageAngle += this.flipTurn;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, UpdateType type) {
        Adapters.UUID.writeNbt(this.house).ifPresent(tag -> nbt.put("house", tag));
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, UpdateType type) {
        this.house = Adapters.UUID.readNbt(nbt.get("house")).orElse(null);
    }

}
