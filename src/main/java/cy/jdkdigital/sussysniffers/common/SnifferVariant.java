package cy.jdkdigital.sussysniffers.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.sussysniffers.SussySniffers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

public record SnifferVariant(ResourceLocation texture, float size)
{
    public static final Codec<SnifferVariant> CODEC = RecordCodecBuilder.create(builder ->
        builder.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(SnifferVariant::texture),
                Codec.FLOAT.fieldOf("size").forGetter(SnifferVariant::size)

        ).apply(builder, SnifferVariant::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SnifferVariant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(SussySniffers.SNIFFER_VARIANT_REGISTRY_KEY);

    public static final DeferredHolder<SnifferVariant, SnifferVariant> DEFAULT_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("default", () -> new SnifferVariant(ResourceLocation.withDefaultNamespace("textures/entity/sniffer/sniffer.png"), 1.0f));
    public static final DeferredHolder<SnifferVariant, SnifferVariant> DARK_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("dark", () -> new SnifferVariant(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/dark.png"), 1.02f));
    public static final DeferredHolder<SnifferVariant, SnifferVariant> PURPLE_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("purple", () -> new SnifferVariant(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/purple.png"), 0.8f));
    public static final DeferredHolder<SnifferVariant, SnifferVariant> AZALEA_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("azalea", () -> new SnifferVariant(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/azalea.png"), 0.95f));
    public static final DeferredHolder<SnifferVariant, SnifferVariant> CHERRY_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("cherry", () -> new SnifferVariant(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/cherry.png"), 0.78f));
    public static final DeferredHolder<SnifferVariant, SnifferVariant> NETHER_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("nether", () -> new SnifferVariant(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/nether.png"), 0.6f));
    public static final DeferredHolder<SnifferVariant, SnifferVariant> END_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("end", () -> new SnifferVariant(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/end.png"), 0.98f));
    public static final DeferredHolder<SnifferVariant, SnifferVariant> SCULK_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("sculk", () -> new SnifferVariant(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/sculk.png"), 1.1f));
    public static final DeferredHolder<SnifferVariant, SnifferVariant> BEDROCK_SNIFFER = SussySniffers.SNIFFER_VARIANTS.register("bedrock", () -> new SnifferVariant(ResourceLocation.fromNamespaceAndPath(SussySniffers.MODID, "textures/entity/sniffer/bedrock.png"), 1.0f));

    public static Holder<SnifferVariant> getVariant(ServerLevelAccessor level, BlockPos pos) {
        if (level.getBlockState(pos.below()).is(Blocks.BEDROCK)) {
            return SnifferVariant.BEDROCK_SNIFFER;
        }
        Holder<Biome> holder = level.getBiome(pos);
        if (holder.is(Tags.Biomes.IS_LUSH) && level.getRandom().nextBoolean()) {
            return SnifferVariant.AZALEA_SNIFFER.getDelegate();
        }
        if (holder.is(Biomes.CHERRY_GROVE)) {
            return SnifferVariant.CHERRY_SNIFFER.getDelegate();
        }
        if (holder.is(Biomes.DEEP_DARK)) {
            return SnifferVariant.SCULK_SNIFFER.getDelegate();
        }
        if (holder.is(BiomeTags.IS_NETHER)) {
            return SnifferVariant.NETHER_SNIFFER.getDelegate();
        }
        if (holder.is(BiomeTags.IS_END)) {
            return SnifferVariant.END_SNIFFER.getDelegate();
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
