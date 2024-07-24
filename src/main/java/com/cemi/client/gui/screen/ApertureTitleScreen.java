package com.cemi.client.gui.screen;

import com.cemi.ApertureCraft;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;;

public class ApertureTitleScreen extends TitleScreen {

    private static final Identifier BACKGROUND =
            new Identifier(ApertureCraft.MOD_ID, "textures/gui/background.png");

    @Override
    protected void init() {
        super.init();
        this.clearChildren();
        this.initWidgetsNormal(this.height / 4, 24);

    }

    private void initWidgetsNormal(int y, int spacingY) {
        this.addDrawableChild(ApertureButtonWidget
                .acBuilder(Text.translatable("menu.singleplayer"),
                        button -> this.client.setScreen(new SelectWorldScreen(this)))
                .dimensions(10, y, 100, 20).build());
        // Text text = this.getMultiplayerDisabledText();
        // boolean bl = text == null;
        // Tooltip tooltip = text != null ? Tooltip.of(text) : null;
        // this.addDrawableChild(
        // ButtonWidget.builder(Text.translatable("menu.multiplayer"), button -> {
        // Screen screen = (Screen) (this.client.options.skipMultiplayerWarning
        // ? new MultiplayerScreen(this)
        // : new MultiplayerWarningScreen(this));
        // this.client.setScreen(screen);
        // }).dimensions(this.width / 2 - 100, y + spacingY * 1, 200, 20).tooltip(tooltip)
        // .build()).active = bl;
        // this.addDrawableChild(ButtonWidget
        // .builder(Text.translatable("menu.online"), button -> this.switchToRealms())
        // .dimensions(this.width / 2 - 100, y + spacingY * 2, 200, 20).tooltip(tooltip)
        // .build()).active = bl;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {


        context.drawTexture(BACKGROUND, 0, 0, width, height, 0.0F, 0.0F, 16, 128, 16, 128);

        for (Element element : this.children()) {
            if (element instanceof ClickableWidget cw) {
                cw.setAlpha(255.0f);
            }
        }

        for (Drawable drawable : this.drawables) {
            drawable.render(context, mouseX, mouseY, delta);
        }
    }

}
