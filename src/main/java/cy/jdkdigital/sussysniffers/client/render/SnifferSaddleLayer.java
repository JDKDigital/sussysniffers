package cy.jdkdigital.sussysniffers.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import cy.jdkdigital.sussysniffers.SussySniffers;
import net.minecraft.client.model.SnifferModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.sniffer.Sniffer;

public class SnifferSaddleLayer extends RenderLayer<Sniffer, SnifferModel<Sniffer>>
{
    ResourceLocation SADDLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/saddle.png");

    public SnifferSaddleLayer(RenderLayerParent<Sniffer, SnifferModel<Sniffer>> renderer, EntityModelSet entityModels) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn, Sniffer sniffer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!sniffer.isInvisible() && sniffer instanceof Saddleable saddleable && saddleable.isSaddled()) {
            VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RenderType.entityCutout(SADDLE_TEXTURE));
            this.getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(sniffer, 0.0F));
        }
    }
}
