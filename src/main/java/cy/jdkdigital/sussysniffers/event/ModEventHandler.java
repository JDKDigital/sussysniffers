package cy.jdkdigital.sussysniffers.event;

import cy.jdkdigital.sussysniffers.SussySniffers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = SussySniffers.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler
{
    @SubscribeEvent
    static void registerRegistries(NewRegistryEvent event) {
        event.register(SussySniffers.SNIFFER_VARIANT_REGISTRY);
    }
}
