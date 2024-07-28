package com.cemi.client.gui.screen;

import org.jetbrains.annotations.Nullable;
import com.cemi.ApertureCraft;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ApertureButtonWidget extends ButtonWidget {

    private static final ButtonTextures TEXTURES =
            new ButtonTextures(new Identifier(ApertureCraft.MOD_ID, "widget/button"),
                    new Identifier(ApertureCraft.MOD_ID, "widget/button_disabled"),
                    new Identifier(ApertureCraft.MOD_ID, "widget/button_highlighted"));

    protected ApertureButtonWidget(int x, int y, int width, int height, Text message,
            PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, message, onPress, narrationSupplier);
    }

    public static ApertureButtonWidget.Builder acBuilder(Text message,
            ButtonWidget.PressAction onPress) {
        return new ApertureButtonWidget.Builder(message, onPress);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawGuiTexture(TEXTURES.get(this.active, this.isSelected()), this.getX(),
                this.getY(), this.getWidth(), this.getHeight());
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.active ? (this.isSelected() ? 0xFFFFFF : 0x797979) : 0x454545;
        this.drawMessage(context, minecraftClient.textRenderer,
                i | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    @Environment(EnvType.CLIENT)
    public static class Builder {
        private final Text message;
        private final PressAction onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private NarrationSupplier narrationSupplier = DEFAULT_NARRATION_SUPPLIER;

        public Builder(Text message, PressAction onPress) {
            this.message = message;
            this.onPress = onPress;
        }

        public Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder dimensions(int x, int y, int width, int height) {
            return this.position(x, y).size(width, height);
        }

        public Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder narrationSupplier(NarrationSupplier narrationSupplier) {
            this.narrationSupplier = narrationSupplier;
            return this;
        }

        public ApertureButtonWidget build() {
            ApertureButtonWidget buttonWidget = new ApertureButtonWidget(this.x, this.y, this.width,
                    this.height, this.message, this.onPress, this.narrationSupplier);
            buttonWidget.setTooltip(this.tooltip);
            return buttonWidget;
        }
    }

}
