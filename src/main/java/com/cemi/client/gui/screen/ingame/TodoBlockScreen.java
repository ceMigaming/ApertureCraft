package com.cemi.client.gui.screen.ingame;

import com.cemi.block.entity.TodoBlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TodoBlockScreen extends Screen {

    TodoBlockEntity blockEntity;

    public TodoBlockScreen(TodoBlockEntity blockEntity) {
        super(Text.literal("TODO"));
        this.blockEntity = blockEntity;
    }

    public ButtonWidget button1;
    public TextWidget textWidget;

    @Override
    protected void init() {
        button1 = ButtonWidget.builder(Text.literal("Button 1"), button -> {
            System.out.println("You clicked button1!");
        })
                .dimensions(width / 2 - 205, 20, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Tooltip of button1")))
                .build();
        textWidget = new TextWidget(width / 2 - 100, 50, 200, 20, Text.literal("Text widget"), textRenderer);

        addDrawableChild(button1);
        addDrawableChild(textWidget);
    }
}
