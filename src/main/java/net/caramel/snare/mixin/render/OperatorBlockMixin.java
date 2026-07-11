package net.caramel.snare.mixin.render;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.tweak.OperatorBlocksModule;
import net.minecraft.block.BarrierBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.LightBlock;
import net.minecraft.block.StructureVoidBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {
    "net.minecraft.block.BarrierBlock",
    "net.minecraft.block.LightBlock",
    "net.minecraft.block.StructureVoidBlock"
})
public class OperatorBlockMixin {
    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void snare$renderAsModel(BlockState state, CallbackInfoReturnable<BlockRenderType> cir) {
        if (SnareClient.modules().get(OperatorBlocksModule.class).filter(Module::isEnabled).isPresent()) {
            cir.setReturnValue(BlockRenderType.MODEL);
        }
    }
}