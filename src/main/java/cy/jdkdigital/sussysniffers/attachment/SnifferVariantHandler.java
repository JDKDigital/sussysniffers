package cy.jdkdigital.sussysniffers.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.sussysniffers.SussySniffers;
import cy.jdkdigital.sussysniffers.common.SnifferVariant;
import cy.jdkdigital.sussysniffers.network.SnifferVariantPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class SnifferVariantHandler
{
    public static final Codec<SnifferVariantHandler> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    RegistryFileCodec.create(SussySniffers.SNIFFER_VARIANT_REGISTRY_KEY, SnifferVariant.CODEC).fieldOf("variant").forGetter(SnifferVariantHandler::variant),
                    Codec.BOOL.fieldOf("isSaddled").forGetter(SnifferVariantHandler::isSaddled)

            ).apply(builder, SnifferVariantHandler::new)
    );
    public static final StreamCodec<ByteBuf, SnifferVariantHandler> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    private Holder<SnifferVariant> variant;
    private boolean isSaddled;

    public SnifferVariantHandler(Holder<SnifferVariant> variant, boolean isSaddled) {
        this.variant = variant;
        this.isSaddled = isSaddled;
    }

    public void sync(Entity entity) {
        if (!entity.level().isClientSide()) {
            SussySniffers.LOGGER.info("sync entity");
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new SnifferVariantPacket(entity.getId(), this));
        }
    }

    public void setVariant(Sniffer sniffer, Holder<SnifferVariant> variant, boolean isSaddled) {
        this.variant = variant;
        this.isSaddled = isSaddled;
        sync(sniffer);
    }

    public Holder<SnifferVariant> variant() {
        return variant;
    }

    public boolean isSaddled() {
        return isSaddled;
    }
}
