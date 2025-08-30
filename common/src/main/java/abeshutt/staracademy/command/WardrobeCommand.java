package abeshutt.staracademy.command;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModOutfits;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.outfit.core.OutfitPiece;
import abeshutt.staracademy.world.data.WardrobeData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Set;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WardrobeCommand extends Command {

    public static SimpleCommandExceptionType INVALID_OUTFIT_ID = new SimpleCommandExceptionType(
            StarAcademyMod.translatableText("command.wardrobe.unlock.fail"));

    //private static final SuggestionProvider<ServerCommandSource> OUTFIT_ID_SUGGESTIONS = (context, builder) ->
    //        CommandSource.suggestMatching(ModOutfits.REGISTRY.keySet().stream(), builder);

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal(StarAcademyMod.ID)
                .then(literal("wardrobe")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("list")
                                .executes(this::onListUnlockedOutfits))
                        .then(literal("unlock")
                                .then(argument("id", StringArgumentType.word())
                                        //.suggests(OUTFIT_ID_SUGGESTIONS)
                                        .executes(this::onUnlockOutfit))
                                //.then(literal("all")
                               //         .executes(this::onUnlockOutfitAll))
                        )));
    }

    private int onListUnlockedOutfits(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity sender = source.getPlayerOrThrow();
        WardrobeData data = ModWorldData.WARDROBE.getGlobal(sender.getWorld());

        data.get(sender.getUuid()).ifPresentOrElse(
                wardrobe -> {
                    source.sendFeedback(() -> Text.literal("== UNLOCKED OUTFITS =="), false);
                    Set<String> equipped = wardrobe.getEquipped();

                    for(String outfitId : wardrobe.getUnlocked()) {
                        source.sendFeedback(() -> Text.literal("- " + outfitId + (equipped.contains(outfitId) ? " (equipped)" : "")), false);
                    }
                },
                () -> source.sendFeedback(() -> Text.literal("No outfit is unlocked"), false)
        );

        return 0;
    }

    private int onUnlockOutfit(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String outfitId = StringArgumentType.getString(context, "id");
        ServerPlayerEntity sender = context.getSource().getPlayerOrThrow();

        //OutfitPiece outfit = ModOutfits.REGISTRY.get(outfitId);
        //if (outfit == null) throw INVALID_OUTFIT_ID.create();

        WardrobeData data = ModWorldData.WARDROBE.getGlobal(sender.getWorld());
        data.setUnlocked(sender, outfitId, true);
        return 0;
    }

    private int onUnlockOutfitAll(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        /*
        ServerPlayerEntity sender = context.getSource().getPlayerOrThrow();

        for (OutfitPiece outfit : ModOutfits.REGISTRY.values()) {
            if (outfit == null) throw INVALID_OUTFIT_ID.create();

            WardrobeData data = ModWorldData.WARDROBE.getGlobal(sender.getWorld());
            data.setUnlocked(sender, outfit.getId(), true);
        }*/

        return 0;
    }

}
