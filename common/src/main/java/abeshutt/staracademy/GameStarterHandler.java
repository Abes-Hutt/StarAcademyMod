package abeshutt.staracademy;

import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.world.StarterEntry;
import abeshutt.staracademy.world.data.PokemonStarterData;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.config.starter.StarterCategory;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.starter.CobblemonStarterHandler;
import com.cobblemon.mod.common.util.ResourceLocationExtensionsKt;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class GameStarterHandler extends CobblemonStarterHandler {

    public static double SCROLL_AMOUNT = -1;
    public static int SELECTED_CATEGORY = -1;
    public static int SELECTED_POKEMON = -1;

    @Override
    public void chooseStarter(ServerPlayerEntity player, String categoryName, int index) {
        //TODO new player data format
        //PlayerData playerData = Cobblemon.playerData.get(player);

        //if(playerData.getStarterSelected()) {
        //    player.sendMessage(LocalizationUtilsKt.lang("ui.starter.alreadyselected")
        //            .formatted(Formatting.RED), true);
        //    return;
        //} else if(playerData.getStarterLocked()) {
        //    player.sendMessage(LocalizationUtilsKt.lang("ui.starter.cannotchoose")
        //            .formatted(Formatting.RED), true);
        //    return;
        //}

        StarterCategory category = null;

        for(StarterCategory value : this.getStarterList(player)) {
            if(value.getName().equals(categoryName)) {
                category = value;
                break;
            }
        }

        if(category == null || index > category.getPokemon().size()) {
            return;
        }

        PokemonProperties properties = category.getPokemon().get(index);
        if(properties.getSpecies() == null) return;
        Identifier speciesId = ResourceLocationExtensionsKt.asIdentifierDefaultingNamespace(
                properties.getSpecies(), Cobblemon.MODID);
        Species species = PokemonSpecies.INSTANCE.getByIdentifier(speciesId);
        if(species == null) return;

        PokemonStarterData data = ModWorldData.POKEMON_STARTER.getGlobal(player.getWorld());
        StarterEntry entry = data.getEntries().get(player.getUuid());

        if(speciesId.equals(data.getPick(player.getUuid()))) {
            data.setPick(player.getUuid(), null);
        } else if(!entry.isOnCooldown(speciesId) && !data.isGranted(speciesId)) {
            data.setPick(player.getUuid(), speciesId);
        }
    }

}
