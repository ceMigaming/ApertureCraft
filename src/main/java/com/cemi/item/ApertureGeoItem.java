package com.cemi.item;

import java.util.function.Consumer;
import java.util.function.Supplier;
import com.cemi.client.render.item.ApertureGeoItemsRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ApertureGeoItem extends ApertureItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ApertureGeoItem(String name, Settings settings) {
        super(name, settings);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private ApertureGeoItemsRenderer renderer;

            public ApertureGeoItemsRenderer getRenderer() {
                return renderer == null ? renderer = new ApertureGeoItemsRenderer(name) : renderer;
            }
        });
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return () -> this;
    }

}
