package cy.jdkdigital.sussysniffers.event;

import cy.jdkdigital.sussysniffers.SussySniffers;
import cy.jdkdigital.sussysniffers.network.SnifferVariantPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = SussySniffers.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler
{
    @SubscribeEvent
    static void registerRegistries(NewRegistryEvent event) {
        event.register(SussySniffers.SNIFFER_VARIANT_REGISTRY);
    }

    @SubscribeEvent
    static void payloadHandler(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(SussySniffers.MODID).versioned("1").optional();
        registrar.playToClient(
                SnifferVariantPacket.TYPE,
                SnifferVariantPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        SnifferVariantPacket::clientHandle,
                        SnifferVariantPacket::serverHandle
                )
        );
    }
}
