package abeshutt.staracademy.entity;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.net.UpdateShootingStarS2CPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ShootingStarEntity extends LivingEntity implements IDefaultedAttributes  {

    private Vec3d start;
    private Vec3d mid;
    private Vec3d end;
    private int tick;

    public ShootingStarEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
        this.start = Vec3d.ZERO;
        this.mid = Vec3d.ZERO;
        this.end = Vec3d.ZERO;
        this.tick = 0;
    }

    @Override
    public DefaultAttributeContainer getDefaultAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 0.0D)
                .build();
    }

    @Override
    public void tick() {
        this.setNoGravity(true);

        if(!this.getWorld().isClient() && this.getServer() != null) {
            double t = this.tick / 300.0D;

            Vec3d pos = this.start.multiply((1 - t) * (1 - t))
                    .add(this.mid.multiply(2 * (1 - t) * t))
                    .add(this.end.multiply(t * t));

            this.setPosition(pos.x, pos.y, pos.z);

            NetworkManager.sendToPlayers(this.getServer().getPlayerManager().getPlayerList(),
                    new UpdateShootingStarS2CPacket(this.getId(), pos));
        }

        for(int i = 0; i < 100; i++) {
            this.getWorld().addParticle(ParticleTypes.FIREWORK,
                    this.getX() - 5 + this.random.nextDouble() * 10,
                    this.getY() - 5 + this.random.nextDouble() * 10,
                    this.getZ() - 5 + this.random.nextDouble() * 10,
                    0.0D, 1.0D, 0.0D);
        }

        super.tick();

        if(!this.getWorld().isClient()) {
            this.tick++;
        }
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        Adapters.VEC_3D.writeNbt(this.start).ifPresent(tag -> nbt.put("modeMinimum", tag));
        Adapters.VEC_3D.writeNbt(this.mid).ifPresent(tag -> nbt.put("mid", tag));
        Adapters.VEC_3D.writeNbt(this.end).ifPresent(tag -> nbt.put("modeMaximum", tag));
        Adapters.INT.writeNbt(this.tick).ifPresent(tag -> nbt.put("tick", tag));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.start = Adapters.VEC_3D.readNbt(nbt.get("modeMinimum")).orElse(Vec3d.ZERO);
        this.mid = Adapters.VEC_3D.readNbt(nbt.get("mid")).orElse(Vec3d.ZERO);
        this.end = Adapters.VEC_3D.readNbt(nbt.get("modeMaximum")).orElse(Vec3d.ZERO);
        this.tick = Adapters.INT.readNbt(nbt.get("tick")).orElse(0);
    }

}
