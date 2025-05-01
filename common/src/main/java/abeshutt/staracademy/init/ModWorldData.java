package abeshutt.staracademy.init;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.world.data.*;

public class ModWorldData extends ModRegistries {

    public static WorldDataType<PlayerProfileData> PLAYER_PROFILE;
    public static WorldDataType<StarBadgeData> STAR_BADGE;
    public static WorldDataType<PokemonStarterData> POKEMON_STARTER;
    public static WorldDataType<SafariData> SAFARI;
    public static WorldDataType<WardrobeData> WARDROBE;
    public static WorldDataType<PartnerData> PARTNER;
    public static WorldDataType<TeamData> TEAM;

    public static void register() {
        PLAYER_PROFILE = new WorldDataType<>(StarAcademyMod.ID + ".player_profile", PlayerProfileData::new);
        STAR_BADGE = new WorldDataType<>(StarAcademyMod.ID + ".star_badge", StarBadgeData::new);
        POKEMON_STARTER = new WorldDataType<>(StarAcademyMod.ID + ".pokemon_starter", PokemonStarterData::new);
        SAFARI = new WorldDataType<>(StarAcademyMod.ID + ".safari", SafariData::new);
        WARDROBE = new WorldDataType<>(StarAcademyMod.ID + ".wardrobe", WardrobeData::new);
        PARTNER = new WorldDataType<>(StarAcademyMod.ID + ".partner", PartnerData::new);
        TEAM = new WorldDataType<>(StarAcademyMod.ID + ".team", TeamData::new);

        PlayerProfileData.init();
        StarBadgeData.init();
        PokemonStarterData.init();
        SafariData.init();
        WardrobeData.init();
        PartnerData.init();
    }

}
