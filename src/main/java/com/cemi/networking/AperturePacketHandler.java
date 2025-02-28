package com.cemi.networking;

import com.cemi.block.entity.TodoBlockEntity;
import com.cemi.item.ApertureItem;
import com.cemi.item.PortalGunItem;
import com.cemi.world.TodoNotesData;
import com.cemi.world.WorldDataManager;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class AperturePacketHandler {
    public static void registerPacketHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(ApertureNetworkingConstants.LEFT_CLICK_ID,
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
                        Hand hand = buf.readEnumConstant(Hand.class);
                        if (player.getStackInHand(hand).getItem() instanceof ApertureItem) {
                            ((ApertureItem) player.getStackInHand(hand).getItem())
                                    .onLeftClick(player.getWorld(), player, hand);
                        }
                    });
                });
        ServerPlayNetworking.registerGlobalReceiver(ApertureNetworkingConstants.RESET_PORTALS_ID,
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
                        Hand hand = buf.readEnumConstant(Hand.class);
                        if (player.getStackInHand(hand).getItem() instanceof PortalGunItem) {
                            ((PortalGunItem) player.getStackInHand(hand).getItem())
                                    .onResetPortals(player.getWorld(), player, hand);
                        }
                    });
                });
        ServerPlayNetworking.registerGlobalReceiver(ApertureNetworkingConstants.TODO_SEND_ID,
                (server, player, handler, buf, responseSender) -> {
                    BlockPos pos = BlockPos.fromLong(buf.readLong());
                    String note = buf.readString();

                    server.execute(() -> {
                        System.out.println("Received from client: " + note);
                        ServerWorld serverWorld = player.getServerWorld();
                        TodoNotesData data = WorldDataManager.getData(serverWorld);
                        data.addNote(pos, note);
                        System.out.println("Data in persistent storage: " + data.getNote(pos));
                        if(player.getWorld().getBlockEntity(pos) instanceof TodoBlockEntity todoBlockEntity) {
                            todoBlockEntity.setText(note);
                        }
                        // PacketByteBuf responseBuf = PacketByteBufs.create();
                        // responseBuf.writeString(note);
                        // ServerPlayNetworking.send(player, ApertureNetworkingConstants.TODO_GET_ID, responseBuf);
                    });
                });
    }
}
