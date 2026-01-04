package abeshutt.staracademy.cosmetic;

import abeshutt.staracademy.util.ClientScheduler;
import com.bedrockk.molang.runtime.MoParams;
import com.bedrockk.molang.runtime.struct.QueryStruct;
import com.bedrockk.molang.runtime.value.DoubleValue;
import com.cobblemon.mod.common.api.molang.MoLangFunctions;
import com.cobblemon.mod.common.api.riding.util.Vec3Spring;
import com.cobblemon.mod.common.api.scheduling.SchedulingTracker;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

import static com.cobblemon.mod.common.util.MoLangExtensionsKt.getIntOrNull;
import static net.minecraft.util.math.Direction.*;

public class CosmeticPosableState extends PosableState {

    private final Entity entity;
    private final SchedulingTracker schedulingTracker;

    public CosmeticPosableState(Entity entity) {
        this.entity = entity;

        this.getFunctions().addFunction("client_player", unused -> new QueryStruct(new HashMap<>())
                .addFunction("velocity_x", params -> this.getStateSpringValue(params,
                        PlayerAnimState::getWorldVelocity, Axis.X))
                .addFunction("velocity_y", params -> this.getStateSpringValue(params,
                        PlayerAnimState::getWorldVelocity, Axis.Y))
                .addFunction("velocity_z", params -> this.getStateSpringValue(params,
                        PlayerAnimState::getWorldVelocity, Axis.Z))
                .addFunction("velocity_right", params -> this.getStateSpringValue(params,
                        PlayerAnimState::getLocalVelocity, Axis.X))
                .addFunction("velocity_up", params -> this.getStateSpringValue(params,
                        PlayerAnimState::getLocalVelocity, Axis.Y))
                .addFunction("velocity_forward", params -> this.getStateSpringValue(params,
                        PlayerAnimState::getLocalVelocity, Axis.Z))
                .addFunction("input_right", params -> this.getStateSpringValue(params,
                        PlayerAnimState::getInput, Axis.X))
                .addFunction("input_up", params -> this.getStateSpringValue(params,
                        PlayerAnimState::getInput, Axis.Y))
                .addFunction("input_forward", params -> this.getStateSpringValue(params,
                        PlayerAnimState::getInput, Axis.Z)));

        MoLangFunctions.INSTANCE.addFunctions(this.getRuntime().getEnvironment().query,
                this.getFunctions().getFunctions());

        PlayerAnimState.of(this.getEntity()).ifPresent(PlayerAnimState::update);
        this.setAge((int)ClientScheduler.getTick());
        this.schedulingTracker = new SchedulingTracker();
    }

    public DoubleValue getStateSpringValue(MoParams params, Function<PlayerAnimState, Vec3Spring> kind, Axis axis) {
        int numOfTicksToAvg = Optional.ofNullable(getIntOrNull(params, 0)).orElse(0);
        PlayerAnimState player = PlayerAnimState.of(this.getEntity()).orElse(null);
        if (player == null) return DoubleValue.ZERO;
        Vec3d velocity = kind.apply(player).getInterpolated(this.getCurrentPartialTicks(), numOfTicksToAvg);
        return new DoubleValue(MathHelper.clamp(switch (axis) {
            case X -> velocity.x;
            case Y -> velocity.y;
            case Z -> velocity.z;
        }, -1.0f, 1.0f));
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public void updatePartialTicks(float partialTick) {
        super.setCurrentPartialTicks(partialTick);
        this.getSchedulingTracker().update(0.0f);
    }

    @Override
    public SchedulingTracker getSchedulingTracker() {
        return this.schedulingTracker;
    }

}
