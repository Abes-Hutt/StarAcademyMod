package abeshutt.staracademy.mixin;

import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.world.data.AcademyHouse;
import abeshutt.staracademy.world.data.HouseData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Team;decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"))
    public MutableText getDisplayName(AbstractTeam team, Text name) {
        MutableText result = Team.decorateName(team, name).copy();

        if(!this.getWorld().isClient()) {
            HouseData data = ModWorldData.HOUSE.getGlobal(this.getWorld());
            AcademyHouse house = data.getFor(this.getUuid()).orElse(null);

            if(house != null) {
                return result.setStyle(result.getStyle().withColor(house.getColor()));
            }
        }

        return result;
    }

}
