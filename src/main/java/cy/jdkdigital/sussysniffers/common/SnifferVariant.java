package cy.jdkdigital.sussysniffers.common;

import cy.jdkdigital.sussysniffers.SussySniffers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

public record SnifferVariant(ResourceLocation texture, float size)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SnifferVariant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(SussySniffers.SNIFFER_VARIANT_REGISTRY_KEY);

    public static final DeferredHolder<SnifferVariant, SnifferVariant> DEFAULT_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("default", () -> new SnifferVariant(ResourceLocation.withDefaultNamespace("textures/entity/sniffer/sniffer.png"), 1.0f));
    public static final DeferredHolder<SnifferVariant, SnifferVariant> DARK_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("dark", () -> new SnifferVariant(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/dark.png"), 1.02f));
    public static final DeferredHolder<SnifferVariant, SnifferVariant> PURPLE_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("purple", () -> new SnifferVariant(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/purple.png"), 0.8f));
    public static final DeferredHolder<SnifferVariant, SnifferVariant> AZALEA_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("azalea", () -> new SnifferVariant(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/azalea.png"), 0.95f));
    public static final DeferredHolder<SnifferVariant, SnifferVariant> CHERRY_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("cherry", () -> new SnifferVariant(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/cherry.png"), 0.78f));
    public static final DeferredHolder<SnifferVariant, SnifferVariant> NETHER_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("nether", () -> new SnifferVariant(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/nether.png"), 0.6f));

    public static Holder<SnifferVariant> getVariant(ServerLevelAccessor level, BlockPos pos) {
        Holder<Biome> holder = level.getBiome(pos);
        if (holder.is(Tags.Biomes.IS_LUSH) && level.getRandom().nextBoolean()) {
            return SnifferVariant.AZALEA_SNIFFER.getDelegate();
        }
        if (holder.is(Biomes.CHERRY_GROVE)) {
            return SnifferVariant.CHERRY_SNIFFER.getDelegate();
        }
        if (holder.is(BiomeTags.IS_NETHER)) {
            return SnifferVariant.NETHER_SNIFFER.getDelegate();
        }
        if (level.getRandom().nextFloat() < 0.05) {
            return SnifferVariant.PURPLE_SNIFFER.getDelegate();
        }
        if (level.getRandom().nextBoolean()) {
            return SnifferVariant.DARK_SNIFFER.getDelegate();
        }
        return SnifferVariant.DEFAULT_SNIFFER.getDelegate();
    }
}
