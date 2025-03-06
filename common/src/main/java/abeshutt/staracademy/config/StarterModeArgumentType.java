package abeshutt.staracademy.config;

import abeshutt.staracademy.world.data.StarterMode;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class StarterModeArgumentType implements ArgumentType<String> {

    private static final String[] VALUES = Arrays.stream(StarterMode.values())
            .map(Enum::name)
            .toArray(String[]::new);

    private StarterModeArgumentType() {}

    public static StarterModeArgumentType starterMode() {
        return new StarterModeArgumentType();
    }

    public static String getStarterMode(CommandContext<ServerCommandSource> context, String id) {
        return StringArgumentType.getString(context, id);
    }

    @Override
    public String parse(com.mojang.brigadier.StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    public static final SuggestionProvider<ServerCommandSource> SUGGESTIONS =
            (context, builder) -> suggestMatching(builder, VALUES);

    private static CompletableFuture<Suggestions> suggestMatching(
            SuggestionsBuilder builder, String[] values) {
        for (String value : values) {
            if (value.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(value);
            }
        }
        return builder.buildFuture();
    }
}
