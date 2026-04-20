package com.cemi.util;

import java.util.function.Consumer;

import org.slf4j.Logger;

import com.cemi.ApertureCraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.resource.ResourceFactory;

public class ShaderHelper {

    public static final MinecraftClient client = MinecraftClient.getInstance();
    static Logger logger = ApertureCraft.LOGGER;

    public static final SignalBiArged<ResourceFactory, Consumer<ShaderProgram>> loadShaderSignal = new SignalBiArged<>();

    private static ShaderProgram portalShader;

    public static ShaderProgram getPortalShader() {
        return portalShader;
    }

    public static void setPortalShader(ShaderProgram program) {
        portalShader = program;
    }
}
