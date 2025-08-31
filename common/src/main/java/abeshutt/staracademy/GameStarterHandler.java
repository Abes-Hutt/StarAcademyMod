package abeshutt.staracademy;

import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.world.StarterEntry;
import abeshutt.staracademy.world.data.PokemonStarterData;
import abeshutt.staracademy.world.data.StarterId;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.storage.player.GeneralPlayerData;
import com.cobblemon.mod.common.config.starter.StarterCategory;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.starter.CobblemonStarterHandler;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import com.cobblemon.mod.common.util.ResourceLocationExtensionsKt;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class GameStarterHandler extends CobblemonStarterHandler {

    public static double SCROLL_AMOUNT = -1;
    public static int SELECTED_CATEGORY = -1;
    public static int SELECTED_POKEMON = -1;

    @Override
    public void chooseStarter(ServerPlayerEntity player, String categoryName, int index) {
        GeneralPlayerData playerData = Cobblemon.playerDataManager.getGenericData(player);

        if(playerData.getStarterSelected()) {
            player.sendMessage(LocalizationUtilsKt.lang("ui.starter.alreadyselected")
                    .formatted(Formatting.RED), true);
            return;
        } else if(playerData.getStarterLocked()) {
            player.sendMessage(LocalizationUtilsKt.lang("ui.starter.cannotchoose")
                    .formatted(Formatting.RED), true);
            return;
        }

        StarterId starter = new StarterId(categoryName, index);
        PokemonStarterData data = ModWorldData.POKEMON_STARTER.getGlobal(player.getWorld());
        StarterEntry entry = data.getEntries().get(player.getUuid());

        if(entry.getGranted() != null) {
            return;
        }

        if(starter.equals(data.getPick(player.getUuid()))) {
            data.setPick(player.getUuid(), null);
        } else if(data.getRemainingAllocations(starter) > 0) {
            data.setPick(player.getUuid(), starter);
        }
    }

}
