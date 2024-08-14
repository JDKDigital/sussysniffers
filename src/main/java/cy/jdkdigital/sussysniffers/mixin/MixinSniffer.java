package cy.jdkdigital.sussysniffers.mixin;

import com.mojang.authlib.GameProfile;
import cy.jdkdigital.sussysniffers.SussySniffers;
import cy.jdkdigital.sussysniffers.common.SnifferVariant;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

//@Debug(export = true)
@Mixin(value = Sniffer.class)
public class MixinSniffer extends Mob implements VariantHolder<Holder<SnifferVariant>>, OwnableEntity, PlayerRideableJumping, Saddleable
{
    private static final EntityDataAccessor<Holder<SnifferVariant>> DATA_VARIANT_ID = SynchedEntityData.defineId(Sniffer.class, SussySniffers.SNIFFER_VARIANT_SERIALIZER.get());
    private static final EntityDataAccessor<Boolean> DATA_SADDLED = SynchedEntityData.defineId(Sniffer.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    private UUID owner;

    protected MixinSniffer(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            at = {@At(value = "HEAD")},
            method = {"dropSeed()V"},
            cancellable = true
    )
    public void dropSussyStuff(CallbackInfo callbackInfo) {
        Sniffer that = (Sniffer) (Object) this;
        if (that.level() instanceof ServerLevel serverLevel && that.getEntityData().get(Sniffer.DATA_DROP_SEED_AT_TICK) == that.tickCount) {
            BlockPos sniffTarget = that.getBrain().getMemory(MemoryModuleType.SNIFFER_SNIFFING_TARGET).orElse(that.getHeadBlock().below());
            if (that.level().getBlockEntity(sniffTarget) instanceof BrushableBlockEntity brushableBlockEntity) {
                Player fakePlayer = FakePlayerFactory.get(serverLevel, new GameProfile(SussySniffers.FAKE_PLAYER_UUID, "sussy_sniffer"));
                brushableBlockEntity.unpackLootTable(fakePlayer);
                Block.popResourceFromFace(serverLevel, sniffTarget, Direction.UP, brushableBlockEntity.getItem());
                if (brushableBlockEntity.getBlockState().getBlock() instanceof BrushableBlock brushableBlock) {
                    that.level().setBlock(sniffTarget, brushableBlock.getTurnsInto().defaultBlockState(), 3);
                }
                callbackInfo.cancel();
            }
        }
    }

    @Inject(
            at = {@At(value = "TAIL")},
            method = {"defineSynchedData(Lnet/minecraft/network/syncher/SynchedEntityData$Builder;)V"}
    )
    public void defineVariantSynchedData(SynchedEntityData.Builder builder, CallbackInfo callbackInfo) {
        builder.define(DATA_VARIANT_ID, SussySniffers.SNIFFER_VARIANT_REGISTRY.getHolderOrThrow(SnifferVariant.DEFAULT_SNIFFER.getKey()));
        builder.define(DATA_SADDLED, false);
    }

    @Inject(
            at = {@At(value = "RETURN")},
            method = {"Lnet/minecraft/world/entity/animal/sniffer/Sniffer;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"}
    )
    public InteractionResult useTameItem(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callbackInfo) {
        if (!callbackInfo.getReturnValue().consumesAction()) {
            // Tame attempt
            if (this.getOwnerUUID() == null && player.getItemInHand(hand).is(SussySniffers.SNIFFER_TAME_ITEMS)) {
                player.getItemInHand(hand).consume(1, player);
                Sniffer that = (Sniffer) (Object) this;
                if (that.getRandom().nextInt(3) == 0 && !EventHooks.onAnimalTame(that, player)) {
                    this.setOwnerUUID(player.getUUID());
                    if (player instanceof ServerPlayer serverplayer) {
                        CriteriaTriggers.TAME_ANIMAL.trigger(serverplayer, that);
                    }
                    this.level().broadcastEntityEvent(that, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(that, (byte) 6);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
            if (this.getOwnerUUID() != null && this.getOwnerUUID().equals(player.getUUID())) {
                if (this.isSaddled()) {
                    if (player.getItemInHand(hand).canPerformAction(ItemAbilities.SHEARS_REMOVE_ARMOR)) {
                        // Remove saddle
                        player.getItemInHand(hand).hurtAndBreak(1, player, getSlotForHand(hand));
                        this.removeSaddle();
                        this.playSound(SoundEvents.ARMOR_UNEQUIP_WOLF);
                        this.spawnAtLocation(Items.SADDLE);
                    } else if (!this.level().isClientSide()) {
                        // Ride
                        player.startRiding(this);
                    }
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                }
            }
        }
        return callbackInfo.getReturnValue();
    }

    @Inject(
            at = {@At(value = "RETURN")},
            method = {"Lnet/minecraft/world/entity/animal/sniffer/Sniffer;canDig()Z"}
    )
    public boolean canDigWhenVehicle(CallbackInfoReturnable<Boolean> callbackInfo) {
        return callbackInfo.getReturnValue() && this.getFirstPassenger() == null;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 7) {
            this.spawnTamingParticles(true);
        } else if (id == 6) {
            this.spawnTamingParticles(false);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setVariant(SnifferVariant.getVariant(level, this.blockPosition()));

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public float getScale() {
        return super.getScale() * getVariant().value().size();
    }

    @Override
    public void setVariant(Holder<SnifferVariant> variant) {
        Sniffer that = (Sniffer) (Object) this;
        that.getEntityData().set(DATA_VARIANT_ID, variant);
    }

    @Override
    public @NotNull Holder<SnifferVariant> getVariant() {
        Sniffer that = (Sniffer) (Object) this;
        return that.getEntityData().get(DATA_VARIANT_ID);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("variant", this.getVariant().unwrapKey().orElse(SnifferVariant.DEFAULT_SNIFFER.getKey()).location().toString());
        if (this.getOwnerUUID() != null) {
            compound.putUUID("owner", this.getOwnerUUID());
        }
        compound.putBoolean("isSaddled", this.isSaddled());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("variant")) {
            SussySniffers.SNIFFER_VARIANT_REGISTRY.getHolder(ResourceLocation.tryParse(compound.getString("variant"))).ifPresent(this::setVariant);
        }
        if (compound.contains("owner")) {
            this.setOwnerUUID(compound.getUUID("owner"));
        }
        if (compound.getBoolean("isSaddled")) {
            this.getEntityData().set(DATA_SADDLED, true);
        }
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.owner;
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.owner = uuid;
    }

    @Override
    public void onPlayerJump(int i) {

    }

    @Override
    public boolean canJump() {
        return false;
    }

    @Override
    public void handleStartJump(int i) {

    }

    @Override
    public void handleStopJump() {

    }

    @Override
    protected void dropEquipment() {
        if (this.isSaddled()) {
            this.spawnAtLocation(Items.SADDLE);
        }
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby() && this.getOwnerUUID() != null;
    }

    @Override
    public void equipSaddle(ItemStack itemStack, @Nullable SoundSource soundSource) {
        this.getEntityData().set(DATA_SADDLED, true);
        if (soundSource != null) {
            this.level().playSound(null, this, SoundEvents.CAMEL_SADDLE, soundSource, 0.5F, 1.0F);
        }
    }

    private void removeSaddle() {
        this.getEntityData().set(DATA_SADDLED, false);
    }

    @Override
    public boolean isSaddled() {
        return this.getEntityData().get(DATA_SADDLED);
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || (this.isSaddled() && this.isVehicle() && this.getFirstPassenger() instanceof Player);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (this.isSaddled()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof Player) {
                return (Player)entity;
            }
        }

        return super.getControllingPassenger();
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        float f = player.xxa * 0.5F;
        float f1 = player.zza;
        if (f1 <= 0.0F) {
            f1 *= 0.25F;
        }

        return new Vec3(f, 0.0, f1);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.2F;
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        Vec2 vec2 = new Vec2(player.getXRot() * 0.5F, player.getYRot());
        this.setRot(vec2.y, vec2.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
    }

    protected void spawnTamingParticles(boolean tamed) {
        ParticleOptions particleoptions = ParticleTypes.HEART;
        if (!tamed) {
            particleoptions = ParticleTypes.SMOKE;
        }

        for(int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(particleoptions, this.getRandomX(1.3), this.getRandomY() + 0.8, this.getRandomZ(1.3), d0, d1, d2);
        }
    }
}
