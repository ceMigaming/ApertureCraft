package com.cemi.server;

import java.util.Map;

import com.cemi.world.WorldDataManager;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ApertureCommands {
	public static void registerCommands() {
		CommandRegistrationCallback.EVENT
				.register(
						(dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("todo")
								.requires(s -> s.hasPermissionLevel(2))
								.executes(context -> {
									context.getSource().sendFeedback(
											() -> {
												MutableText text = Text.literal("Notes: \n");
												var data = WorldDataManager.getData(context.getSource().getWorld())
														.getNotes();
												for (Map.Entry<BlockPos, String> entry : data.entrySet()) {
													text.append(Text
															.literal(entry.getKey().toShortString() + " : "
																	+ entry.getValue() + "\n")
															.setStyle(Style.EMPTY.withClickEvent(
																	new ClickEvent(ClickEvent.Action.RUN_COMMAND,
																			"/tp " + entry.getKey().toShortString().replace(",", ""))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Teleport"))).withUnderline(true)));
												}
												return text;
											},
											false);
									return 1;
								})));
	}
}
