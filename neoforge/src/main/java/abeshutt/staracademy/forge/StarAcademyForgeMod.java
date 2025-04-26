package abeshutt.staracademy.forge;

import abeshutt.staracademy.StarAcademyMod;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(StarAcademyMod.ID)
public final class StarAcademyForgeMod {

    public StarAcademyForgeMod() {
        EventBuses.registerModEventBus(StarAcademyMod.ID, FMLJavaModLoadingContext.get().getModEventBus());
        StarAcademyMod.init();
    }

}
