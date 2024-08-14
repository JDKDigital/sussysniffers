package cy.jdkdigital.sussysniffers.mixin;

import cy.jdkdigital.sussysniffers.common.SnifferVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.level.block.SnifferEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = SnifferEggBlock.class)
public class MixinSnifferEggBlock
{
    @Inject(
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z")},
            method = {"Lnet/minecraft/world/level/block/SnifferEggBlock;tick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V"},
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void hatchSniffer(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo callbackInfo, Sniffer sniffer) {
        if (sniffer instanceof VariantHolder<?> variantHolder) {
            VariantHolder<Holder<SnifferVariant>> snifferVariant = (VariantHolder<Holder<SnifferVariant>>) variantHolder;
            snifferVariant.setVariant(SnifferVariant.getVariant(level, pos));
        }
    }
}
