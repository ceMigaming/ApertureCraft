package com.cemi.block.entity;

import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation.LoopType;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ApertureCubeDropperBlockEntity extends BlockEntity implements GeoBlockEntity {

    protected static final RawAnimation IDLE_OPEN = RawAnimation.begin().then("animation.cube_dropper.idle_open",
            LoopType.LOOP);
    protected static final RawAnimation OPEN = RawAnimation.begin().then("animation.cube_dropper.open",
            LoopType.PLAY_ONCE);
    protected static final RawAnimation IDLE_CLOSED = RawAnimation.begin().then("animation.cube_dropper.idle_closed",
            LoopType.LOOP);
    protected static final RawAnimation CLOSE = RawAnimation.begin().then("animation.cube_dropper.close",
            LoopType.PLAY_ONCE);
    protected static final RawAnimation DISPENSE = RawAnimation.begin().then("animation.cube_dropper.dispense",
            LoopType.PLAY_ONCE);

    private BlockPos masterPos;
    private UUID trackedEntityUuid;
    private boolean triggered = false;

    private EntityType<?> spawnableEntity;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ApertureCubeDropperBlockEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.CUBE_DROPPER_BLOCK_ENTITY, pos, state);
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos = masterPos;
    }

    public BlockPos getMasterPos() {
        return this.masterPos;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "controller", event -> event.setAndContinue(IDLE_CLOSED))
                        .triggerableAnim("open", OPEN).triggerableAnim("close", CLOSE).triggerableAnim("idle_open",
                                IDLE_OPEN)
                        .triggerableAnim("idle_close", IDLE_CLOSED));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putLong("masterPos", this.masterPos.asLong());
        if (trackedEntityUuid != null)
            nbt.putString("trackedEntity", this.trackedEntityUuid.toString());
        if (this.spawnableEntity != null) {
            Identifier id = Registries.ENTITY_TYPE.getId(this.spawnableEntity);
            nbt.putString("spawnableEntity", id.toString());
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.masterPos = BlockPos.fromLong(nbt.getLong("masterPos"));
        if (nbt.contains("trackedEntity"))
            this.trackedEntityUuid = UUID.fromString(nbt.getString("trackedEntity"));
        if (nbt.contains("spawnableEntity")) {
            Identifier id = new Identifier(nbt.getString("spawnableEntity"));
            this.spawnableEntity = Registries.ENTITY_TYPE.get(id);
        }
    }

    public void killEntity() {
        var trackedEntityOpt = world
                .getEntitiesByType(spawnableEntity, Box.enclosing(pos.add(-100, -100, -100), pos.add(100, 100, 100)),
                        en -> en.getUuid().equals(trackedEntityUuid))
                .stream().findFirst();
        if (trackedEntityOpt.isPresent()) {
            var trackedEntity = trackedEntityOpt.get();
            trackedEntity.kill();
        }
    }

    public void spawnEntity() {
        var trackedEntityOpt = world
                .getEntitiesByType(spawnableEntity, Box.enclosing(pos.add(-100, -100, -100), pos.add(100, 100, 100)),
                        en -> en.getUuid().equals(trackedEntityUuid))
                .stream().findFirst();
        var trackedEntity = trackedEntityOpt.isPresent() ? trackedEntityOpt.get() : null;
        if (trackedEntity != null) {
            trackedEntity.kill();
        }
        trackedEntity = spawnableEntity.create(world);
        trackedEntity.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0);
        world.spawnEntity(trackedEntity);
        trackedEntityUuid = trackedEntity.getUuid();
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public boolean getTriggered() {
        return this.triggered;
    }

    public void setSpawnableEntity(EntityType<?> entityType) {
        this.spawnableEntity = entityType;
    }
}
