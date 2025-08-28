package abeshutt.staracademy.config;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.config.sound.DimensionEnterPlaySoundEvent;
import abeshutt.staracademy.config.sound.PlaySoundEvent;
import abeshutt.staracademy.data.adapter.util.ServerSound;
import com.google.gson.annotations.Expose;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class SoundEventConfig extends FileConfig {

    @Expose private List<PlaySoundEvent> events;

    @Override
    public String getPath() {
        return "sound_event";
    }

    public <T extends PlaySoundEvent> List<T> get(Class<T> type) {
        List<T> result = new ArrayList<>();

        for(PlaySoundEvent event : this.events) {
            if(type.isAssignableFrom(event.getClass())) {
                result.add((T)event);
            }
        }

        return result;
    }

    @Override
    protected void reset() {
        this.events = new ArrayList<>();
        this.events.add(new DimensionEnterPlaySoundEvent(new ServerSound(
                SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP.value().getId(),
                SoundCategory.MASTER,
                1.0F, 1.0F, new Vec3d(0, 0, 0), false, 0,
                ServerSound.AttenuationType.NONE, true
        ), StarAcademyMod.SAFARI.getValue()));
    }

}
