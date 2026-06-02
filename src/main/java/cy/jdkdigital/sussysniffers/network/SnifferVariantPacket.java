package cy.jdkdigital.sussysniffers.network;

import cy.jdkdigital.sussysniffers.SussySniffers;
import cy.jdkdigital.sussysniffers.attachment.SnifferVariantHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SnifferVariantPacket(int entityID, SnifferVariantHandler data) implements CustomPacketPayload
{
    public static final Type<SnifferVariantPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "sniffer_variant"));

    public static final StreamCodec<FriendlyByteBuf, SnifferVariantPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            SnifferVariantPacket::entityID,
            SnifferVariantHandler.STREAM_CODEC,
            SnifferVariantPacket::data,
            SnifferVariantPacket::new
    );

    public static void clientHandle(final SnifferVariantPacket data, final IPayloadContext context) {
        SussySniffers.LOGGER.info("clientHandle" + data.data);
        ClientLevel level = Minecraft.getInstance().level;

        if (level != null) {
            Entity entity = level.getEntity(data.entityID);
            SussySniffers.LOGGER.info("update entity " + data.data);

            if (entity != null) {
                entity.setData(SussySniffers.SNIFFER_VARIANT_HANDLER, data.data);
            }
        }
    }

    public static void serverHandle(final SnifferVariantPacket data, final IPayloadContext context) {
        SussySniffers.LOGGER.info("serverHandle" + data.data);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
