package com.lvu;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;

public class CustomSuggestionProviders {
    public static SuggestionProvider<FabricClientCommandSource> XRAY_OPTIONS =
            (ctx, builder) -> CommandSource.suggestMatching("add\nmodify".lines(), builder);

    public static SuggestionProvider<FabricClientCommandSource> MOD_OPTIONS =
            (ctx, builder) -> CommandSource.suggestMatching("xray".lines(), builder);

    public static SuggestionProvider<FabricClientCommandSource> ENABLE_OR_DISABLE =
            (ctx, builder) -> CommandSource.suggestMatching("enable\ndisable".lines(), builder);
}
