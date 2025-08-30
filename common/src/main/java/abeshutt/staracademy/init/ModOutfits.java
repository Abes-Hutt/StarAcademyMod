package abeshutt.staracademy.init;

import abeshutt.staracademy.outfit.core.OutfitPiece;
import abeshutt.staracademy.outfit.models.*;

import java.util.HashMap;
import java.util.Map;

public class ModOutfits {

    private static Map<String, OutfitPiece> REGISTRY = new HashMap<>();

    public static Classy1Outfit.Hat CLASSY1_HAT;
    public static Classy1Outfit.Shirt CLASSY1_SHIRT;
    public static Classy1Outfit.Pants CLASSY1_PANTS;
    public static Classy1Outfit.Shoes CLASSY1_SHOES;

    public static Classy2Outfit.Hat CLASSY2_HAT;
    public static Classy2Outfit.Shirt CLASSY2_SHIRT;
    public static Classy2Outfit.Skirt CLASSY2_SKIRT;
    public static Classy2Outfit.Shoes CLASSY2_SHOES;

    public static Comfy1Outfit.Hat COMFY1_HAT;
    public static Comfy1Outfit.Shirt COMFY1_SHIRT;
    public static Comfy1Outfit.Pants COMFY1_PANTS;
    public static Comfy1Outfit.Shoes COMFY1_SHOES;

    public static Comfy2Outfit.Hat COMFY2_HAT;
    public static Comfy2Outfit.Shirt COMFY2_SHIRT;
    public static Comfy2Outfit.Skirt COMFY2_SKIRT;
    public static Comfy2Outfit.Shoes COMFY2_SHOES;

    public static Formal1Outfit.Jacket FORMAL1_JACKET;
    public static Formal1Outfit.Pants FORMAL1_PANTS;
    public static Formal1Outfit.Boots FORMAL1_BOOTS;

    public static Formal2Outfit.Jacket FORMAL2_JACKET;
    public static Formal2Outfit.Skirt FORMAL2_SKIRT;
    public static Formal2Outfit.Boots FORMAL2_BOOTS;

    public static Sporty1Outfit.Cap SPORTY1_CAP;
    public static Sporty1Outfit.Shirt SPORTY1_SHIRT;
    public static Sporty1Outfit.Pants SPORTY1_PANTS;
    public static Sporty1Outfit.Shoes SPORTY1_SHOES;

    public static Sporty2Outfit.Cap SPORTY2_CAP;
    public static Sporty2Outfit.Shirt SPORTY2_SHIRT;
    public static Sporty2Outfit.Skirt SPORTY2_SKIRT;
    public static Sporty2Outfit.Shoes SPORTY2_SHOES;

    public static SailorOutfit.Shirt SAILOR_SHIRT;
    public static SailorOutfit.Skirt SAILOR_SKIRT;
    public static SailorOutfit.Shoes SAILOR_SHOES;

    public static PokemonHatOutfit.Gengar GENGAR_HAT;
    public static PokemonHatOutfit.Goomy GOOMY_HAT;
    public static PokemonHatOutfit.Pikachu PIKACHU_HAT;
    public static PokemonHatOutfit.Slowking SLOWKING_HAT;

    public static SweaterOutfit.Top SWEATER_TOP;
    public static SweaterOutfit.Boots SWEATER_BOOTS;

    public static HeadBandOutfit.Carrot CARROT_HEAD_BAND;
    public static HeadBandOutfit.Flower FLOWER_HEAD_BAND;
    public static HeadBandOutfit.CatEars CAT_EARS;

    public static EeveeHatOutfit.Vaporeon VAPOREON_HAT;
    public static EeveeHatOutfit.Umbreon UMBREON_HAT;
    public static EeveeHatOutfit.Glaceon GLACEON_HAT;
    public static EeveeHatOutfit.Eevee EEVEE_HAT;
    public static EeveeHatOutfit.Espeon ESPEON_HAT;
    public static EeveeHatOutfit.Flareon FLAREON_HAT;
    public static EeveeHatOutfit.Jolteon JOLTEON_HAT;
    public static EeveeHatOutfit.Leafeon LEAFEON_HAT;
    public static EeveeHatOutfit.Sylveon SYLVEON_HAT;

    public static void register() {
        CLASSY1_HAT = register(new Classy1Outfit.Hat("classy1_hat"));
        CLASSY1_SHIRT = register(new Classy1Outfit.Shirt("classy1_shirt"));
        CLASSY1_PANTS = register(new Classy1Outfit.Pants("classy1_pants"));
        CLASSY1_SHOES = register(new Classy1Outfit.Shoes("classy1_shoes"));

        CLASSY2_HAT = register(new Classy2Outfit.Hat("classy2_hat"));
        CLASSY2_SHIRT = register(new Classy2Outfit.Shirt("classy2_shirt"));
        CLASSY2_SKIRT = register(new Classy2Outfit.Skirt("classy2_skirt"));
        CLASSY2_SHOES = register(new Classy2Outfit.Shoes("classy2_shoes"));

        COMFY1_HAT = register(new Comfy1Outfit.Hat("comfy1_hat"));
        COMFY1_SHIRT = register(new Comfy1Outfit.Shirt("comfy1_shirt"));
        COMFY1_PANTS = register(new Comfy1Outfit.Pants("comfy1_pants"));
        COMFY1_SHOES = register(new Comfy1Outfit.Shoes("comfy1_shoes"));

        COMFY2_HAT = register(new Comfy2Outfit.Hat("comfy2_hat"));
        COMFY2_SHIRT = register(new Comfy2Outfit.Shirt("comfy2_shirt"));
        COMFY2_SKIRT = register(new Comfy2Outfit.Skirt("comfy2_skirt"));
        COMFY2_SHOES = register(new Comfy2Outfit.Shoes("comfy2_shoes"));

        FORMAL1_JACKET = register(new Formal1Outfit.Jacket("formal1_jacket"));
        FORMAL1_PANTS = register(new Formal1Outfit.Pants("formal1_pants"));
        FORMAL1_BOOTS = register(new Formal1Outfit.Boots("formal1_boots"));

        FORMAL2_JACKET = register(new Formal2Outfit.Jacket("formal2_jacket"));
        FORMAL2_SKIRT = register(new Formal2Outfit.Skirt("formal2_skirt"));
        FORMAL2_BOOTS = register(new Formal2Outfit.Boots("formal2_boots"));

        SPORTY1_CAP = register(new Sporty1Outfit.Cap("sporty1_cap"));
        SPORTY1_SHIRT = register(new Sporty1Outfit.Shirt("sporty1_shirt"));
        SPORTY1_PANTS = register(new Sporty1Outfit.Pants("sporty1_pants"));
        SPORTY1_SHOES = register(new Sporty1Outfit.Shoes("sporty1_shoes"));

        SPORTY2_CAP = register(new Sporty2Outfit.Cap("sporty2_cap"));
        SPORTY2_SHIRT = register(new Sporty2Outfit.Shirt("sporty2_shirt"));
        SPORTY2_SKIRT = register(new Sporty2Outfit.Skirt("sporty2_skirt"));
        SPORTY2_SHOES = register(new Sporty2Outfit.Shoes("sporty2_shoes"));

        SAILOR_SHIRT = register(new SailorOutfit.Shirt("sailor_shirt"));
        SAILOR_SKIRT = register(new SailorOutfit.Skirt("sailor_skirt"));
        SAILOR_SHOES = register(new SailorOutfit.Shoes("sailor_shoes"));

        GENGAR_HAT = register(new PokemonHatOutfit.Gengar("gengar_hat"));
        GOOMY_HAT = register(new PokemonHatOutfit.Goomy("goomy_hat"));
        PIKACHU_HAT = register(new PokemonHatOutfit.Pikachu("pikachu_hat"));
        SLOWKING_HAT = register(new PokemonHatOutfit.Slowking("slowking_hat"));

        SWEATER_TOP = register(new SweaterOutfit.Top("sweater_top"));
        SWEATER_BOOTS = register(new SweaterOutfit.Boots("sweater_boots"));

        CARROT_HEAD_BAND = register(new HeadBandOutfit.Carrot("carrot_head_band"));
        FLOWER_HEAD_BAND = register(new HeadBandOutfit.Flower("flower_head_band"));
        CAT_EARS = register(new HeadBandOutfit.CatEars("cat_ears"));

        VAPOREON_HAT = register(new EeveeHatOutfit.Vaporeon("vaporeon_hat"));
        UMBREON_HAT = register(new EeveeHatOutfit.Umbreon("umbreon_hat"));
        GLACEON_HAT = register(new EeveeHatOutfit.Glaceon("glaceon_hat"));
        EEVEE_HAT = register(new EeveeHatOutfit.Eevee("eevee_hat"));
        ESPEON_HAT = register(new EeveeHatOutfit.Espeon("espeon_hat"));
        FLAREON_HAT = register(new EeveeHatOutfit.Flareon("flareon_hat"));
        JOLTEON_HAT = register(new EeveeHatOutfit.Jolteon("jolteon_hat"));
        LEAFEON_HAT = register(new EeveeHatOutfit.Leafeon("leafeon_hat"));
        SYLVEON_HAT = register(new EeveeHatOutfit.Sylveon("sylveon_hat"));
    }

    private static int order;

    public static <T extends OutfitPiece> T register(T piece) {
        piece.setOrder(order++);
        REGISTRY.put(piece.getId(), piece);
        return piece;
    }

}
