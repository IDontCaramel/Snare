package net.caramel.snare.mixin.render;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.tweak.OperatorBlocksModule;
import net.minecraft.block.Block;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "getBlockParticle", at = @At("HEAD"), cancellable = true)
    private void snare$suppressBlockParticle(CallbackInfoReturnable<Block> cir) {
        if (SnareClient.modules().get(OperatorBlocksModule.class).filter(Module::isEnabled).isPresent()) {
            cir.setReturnValue(null);
        }
    }
}