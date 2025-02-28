package com.cemi.client.gui.screen.ingame;

import com.cemi.block.entity.TodoBlockEntity;
import com.cemi.networking.ApertureNetworkingConstants;
import com.cemi.world.TodoNotesData;
import com.cemi.world.WorldDataManager;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class TodoBlockScreen extends Screen {

    private TodoBlockEntity blockEntity;

    public TodoBlockScreen(TodoBlockEntity blockEntity) {
        super(Text.literal("TODO"));
        this.blockEntity = blockEntity;
    }

    public ButtonWidget button1;
    public TextFieldWidget textWidget;

    @Override
    protected void init() {
        super.init();

        textWidget = new TextFieldWidget(textRenderer, width / 2 - 100, 50, 200, 20, Text.literal("Note"));
        textWidget.setMaxLength(512);
        // TODO synchronize
        textWidget.setText(blockEntity.getText());

        button1 = ButtonWidget.builder(Text.literal("Save"), button -> {
            blockEntity.setText(textWidget.getText());
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeLong(blockEntity.getPos().asLong());
            buf.writeString(textWidget.getText());
            ClientPlayNetworking.send(ApertureNetworkingConstants.TODO_SEND_ID, buf);
            close();
        })
                .dimensions(width / 2 - 100, 80, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Save note")))
                .build();
        addDrawableChild(button1);
        addDrawableChild(textWidget);
    }
}
