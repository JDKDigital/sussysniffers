package cy.jdkdigital.sussysniffers.event;


import cy.jdkdigital.sussysniffers.SussySniffers;
import cy.jdkdigital.sussysniffers.client.render.SnifferSaddleLayer;
import net.minecraft.client.model.SnifferModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = SussySniffers.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler
{
    @SubscribeEvent
    public static void addSnifferLayers(EntityRenderersEvent.AddLayers event) {
        try {
            var snifferRenderer = (MobRenderer<Sniffer, SnifferModel<Sniffer>>) event.getRenderer(EntityType.SNIFFER);
            if (snifferRenderer != null) {
                snifferRenderer.addLayer(new SnifferSaddleLayer(snifferRenderer, event.getEntityModels()));
            } else {
                SussySniffers.LOGGER.warn("Sniffer renderer not found");
            }
        } catch (Exception e) {
            SussySniffers.LOGGER.warn("Sniffer renderer is not a SnifferRenderer");
        }
    }
}
