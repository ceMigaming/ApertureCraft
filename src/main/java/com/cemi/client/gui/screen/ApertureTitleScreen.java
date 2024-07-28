package com.cemi.client.gui.screen;

import java.lang.management.ManagementFactory;
import org.jetbrains.annotations.Nullable;
import com.cemi.ApertureCraft;
import com.mojang.authlib.minecraft.BanDetails;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
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
        int y = height / 4;
        int spacingY = 24;
        int buttonWidth = 200;
        int buttonHeight = 20;
        this.addDrawableChild(ApertureButtonWidget
                .acBuilder(Text.translatable("menu.singleplayer"),
                        button -> this.client.setScreen(new SelectWorldScreen(this)))
                .dimensions(10, y, buttonWidth, buttonHeight).build());
        Text text = this.getMultiplayerDisabledText();
        boolean isEnabled = text == null && ManagementFactory.getRuntimeMXBean().getInputArguments()
                .toString().contains("-agentlib:jdwp");
        Tooltip tooltip = text != null ? Tooltip.of(text) : null;
        this.addDrawableChild(
                ApertureButtonWidget.acBuilder(Text.translatable("menu.multiplayer"), button -> {
                    Screen screen = (Screen) (this.client.options.skipMultiplayerWarning
                            ? new MultiplayerScreen(this)
                            : new MultiplayerWarningScreen(this));
                    this.client.setScreen(screen);
                }).dimensions(10, y + spacingY, buttonWidth, buttonHeight).tooltip(tooltip)
                        .build()).active = isEnabled;
        this.addDrawableChild(ApertureButtonWidget
                .acBuilder(Text.translatable("menu.options"),
                        button -> this.client
                                .setScreen(new OptionsScreen(this, this.client.options)))
                .dimensions(10, y + spacingY * 2, 200, 20).build());
        this.addDrawableChild(ApertureButtonWidget
                .acBuilder(Text.translatable("menu.quit"), button -> this.client.scheduleStop())
                .dimensions(10, y + spacingY * 3, 200, 20).build());
    }

    @Nullable
    private Text getMultiplayerDisabledText() {
        if (this.client.isMultiplayerEnabled()) {
            return null;
        } else if (this.client.isUsernameBanned()) {
            return Text.translatable("title.multiplayer.disabled.banned.name");
        } else {
            BanDetails banDetails = this.client.getMultiplayerBanDetails();
            if (banDetails != null) {
                return banDetails.expires() != null
                        ? Text.translatable("title.multiplayer.disabled.banned.temporary")
                        : Text.translatable("title.multiplayer.disabled.banned.permanent");
            } else {
                return Text.translatable("title.multiplayer.disabled");
            }
        }
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
