package cy.jdkdigital.sussysniffers.mixin.client;

import cy.jdkdigital.sussysniffers.common.SnifferVariant;
import net.minecraft.client.renderer.entity.SnifferRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

//@Debug(export = true)
@Mixin(value = SnifferRenderer.class)
public class MixinSnifferRenderer
{
    @Overwrite
    public ResourceLocation getTextureLocation(Sniffer sniffer) {
//        if (sniffer.hasCustomName() && sniffer.getCustomName().getString().equals("name")) {
//        }
        if (sniffer instanceof VariantHolder<?> variantHolder) {
            Holder<SnifferVariant> variant = (Holder<SnifferVariant>)variantHolder.getVariant();
            return variant.value().texture();
        }
        return SnifferVariant.DEFAULT_SNIFFER.get().texture();
    }
}
