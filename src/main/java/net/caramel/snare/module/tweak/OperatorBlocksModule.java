package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.minecraft.client.MinecraftClient;

public final class OperatorBlocksModule extends Module {
    public OperatorBlocksModule(Runnable saveRequest) {
        super("operator_blocks", "Operator Blocks", "Renders operator-only blocks as models.", ModuleCategories.RENDER, saveRequest);
    }

    @Override protected void onEnable() { reloadRenderer(); }
    @Override protected void onDisable() { reloadRenderer(); }

    private static void reloadRenderer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) client.worldRenderer.reload();
    }
}