package cy.jdkdigital.sussysniffers;

import cy.jdkdigital.sussysniffers.common.SnifferVariant;
import cy.jdkdigital.sussysniffers.attachment.SnifferVariantHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SussySniffers.MODID)
public class SussySniffers
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "sussysniffers";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final UUID FAKE_PLAYER_UUID = UUID.nameUUIDFromBytes("sussy_sniffer".getBytes(StandardCharsets.UTF_8));

    public static final ResourceKey<Registry<SnifferVariant>> SNIFFER_VARIANT_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "sniffer_variant"));

    public static final Registry<SnifferVariant> SNIFFER_VARIANT_REGISTRY = new RegistryBuilder<>(SNIFFER_VARIANT_REGISTRY_KEY).sync(true).defaultKey(ResourceLocation.fromNamespaceAndPath(MODID, "default")).create();
    public static final DeferredRegister<SnifferVariant> SNIFFER_VARIANTS = DeferredRegister.create(SNIFFER_VARIANT_REGISTRY, MODID);

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, MODID);
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Holder<SnifferVariant>>> SNIFFER_VARIANT_SERIALIZER = ENTITY_DATA_SERIALIZERS.register("sniffer_variant", () -> EntityDataSerializer.forValueType(SnifferVariant.STREAM_CODEC));

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
    public static final Supplier<AttachmentType<SnifferVariantHandler>> SNIFFER_VARIANT_HANDLER = ATTACHMENT_TYPES.register("sniffer_variant_handler", () -> AttachmentType.builder(() -> new SnifferVariantHandler(SnifferVariant.DEFAULT_SNIFFER, false)).serialize(SnifferVariantHandler.CODEC).build());


    public static final TagKey<Item> SNIFFER_TAME_ITEMS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(MODID, "sniffer_tame"));

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SussySniffers(IEventBus modEventBus, ModContainer modContainer)
    {
        SNIFFER_VARIANTS.register(modEventBus);
        ENTITY_DATA_SERIALIZERS.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
