package com.cemi.client.model;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

@Environment(EnvType.CLIENT)
public class SlopeModel implements UnbakedModel, BakedModel, FabricBakedModel {

    private final SpriteIdentifier SPRITE_ID;
    private Sprite sprite;

    public SlopeModel(Identifier id) {
        SPRITE_ID = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, id);
    }

    private Mesh mesh;

    @Override
    public Collection<Identifier> getModelDependencies() {
        return List.of(); // This model does not depend on other models.
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
        // This is related to model parents, it's not required for our use case
    }

    @Override
    public BakedModel bake(Baker baker,
            Function<SpriteIdentifier, Sprite> textureGetter,
            ModelBakeSettings rotationContainer,
            Identifier modelId) {

        sprite = textureGetter.apply(SPRITE_ID);

        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        // ===== TOP SLOPE (north low -> south high) =====
        emitter.pos(0, 0f, 0f, 0f); // NE low
        emitter.pos(1, 0f, 1f, 1f); // NW low
        emitter.pos(2, 1f, 1f, 1f); // SW high
        emitter.pos(3, 1f, 0f, 0f); // SE high
        emitter.nominalFace(Direction.UP);
        emitter.spriteBake(sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.color(-1, -1, -1, -1);
        emitter.emit();

        // ===== BOTTOM =====
        emitter.square(Direction.DOWN, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
        emitter.spriteBake(sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.color(-1, -1, -1, -1);
        emitter.cullFace(Direction.DOWN);
        emitter.emit();

        // ===== NORTH FACE (vertical) =====
        emitter.pos(0, 0f, 0f, 0f);
        emitter.pos(1, 1f, 0f, 0f);
        emitter.pos(2, 1f, 0f, 0f);
        emitter.pos(3, 0f, 0f, 0f);
        emitter.nominalFace(Direction.NORTH);
        emitter.spriteBake(sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.color(-1, -1, -1, -1);
        emitter.cullFace(Direction.NORTH);
        emitter.emit();

        // ===== SOUTH FACE (full height) =====
        emitter.square(Direction.SOUTH, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
        emitter.spriteBake(sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.color(-1, -1, -1, -1);
        emitter.cullFace(Direction.SOUTH);
        emitter.emit();

        // ===== WEST FACE (triangle) =====
        emitter.pos(0, 0f, 0f, 0f);
        emitter.pos(1, 0f, 0f, 1f);
        emitter.pos(2, 0f, 1f, 1f);
        emitter.pos(3, 0f, 1f, 1f); // duplicate for triangle
        emitter.nominalFace(Direction.WEST);
        emitter.spriteBake(sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.color(-1, -1, -1, -1);
        emitter.cullFace(Direction.WEST);
        emitter.emit();

        // ===== EAST FACE (triangle) =====
        emitter.pos(0, 1f, 0f, 0f);
        emitter.pos(1, 1f, 1f, 1f);
        emitter.pos(2, 1f, 0f, 1f);
        emitter.pos(3, 1f, 0f, 1f); // duplicate
        emitter.nominalFace(Direction.EAST);
        emitter.spriteBake(sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.color(-1, -1, -1, -1);
        emitter.cullFace(Direction.EAST);
        emitter.emit();

        mesh = builder.build();
        return this;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
        // Don't need because we use FabricBakedModel instead. However, it's better to
        // not return null in case some mod decides to call this function.
        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true; // we want the block to have a shadow depending on the adjacent blocks
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    // We will also implement this method to have the correct lighting in the item
    // rendering. Try to set this to false and you will see the difference.
    @Override
    public boolean isSideLit() {
        return true;
    }

    @Override
    public Sprite getParticleSprite() {
        return sprite; // Block break particle, let's use furnace_top
    }

    // We need to implement getTransformation() and getOverrides()
    @Override
    public ModelTransformation getTransformation() {
        return ModelHelper.MODEL_TRANSFORM_BLOCK;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false; // False to trigger FabricBakedModel rendering
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos,
            Supplier<Random> supplier, RenderContext renderContext) {
        // Render function

        // We just render the mesh
        mesh.outputTo(renderContext.getEmitter());
    }

    // Finally, we can implement the item render function
    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> supplier, RenderContext renderContext) {
        mesh.outputTo(renderContext.getEmitter());
    }
}