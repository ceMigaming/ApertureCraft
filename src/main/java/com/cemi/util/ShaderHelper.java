package com.cemi.util;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import com.cemi.ApertureCraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.Uniform;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;

public class ShaderHelper {

    public static final MinecraftClient client = MinecraftClient.getInstance();
    static Logger logger = ApertureCraft.LOGGER;

    public static final SignalBiArged<ResourceFactory, Consumer<ShaderProgram>> loadShaderSignal =
            new SignalBiArged<>();

    private static PortalShader portalShader;

    public static void initShaders() {
        loadShaderSignal.connect((resourceManager, resultConsumer) -> {
            try {
                PortalShader shader = new PortalShader(getResourceFactory(resourceManager),
                        "portal", VertexFormats.POSITION_COLOR);
                resultConsumer.accept(shader);
                portalShader = shader;
                System.out.println("TEST!!!!!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static class PortalShader extends ShaderProgram {

        public final Uniform closeAlpha;
        public final Uniform theColorR;
        public final Uniform theColorG;
        public final Uniform theColorB;
        public final Uniform time;
        public final Uniform pass;

        public PortalShader(ResourceFactory factory, String name, VertexFormat format)
                throws IOException {
            super(factory, name, format);
            closeAlpha = getUniform("closeAlpha");
            theColorR = getUniform("theColorR");
            theColorG = getUniform("theColorG");
            theColorB = getUniform("theColorB");
            time = getUniform("time");
            pass = getUniform("pass");
        }

    }

    private static ResourceFactory getResourceFactory(ResourceFactory resourceManager) {
        ResourceFactory resourceFactory = new ResourceFactory() {
            @Override
            public Optional<Resource> getResource(Identifier resourceLocation) {
                Identifier corrected =
                        new Identifier(ApertureCraft.MOD_ID, resourceLocation.getPath());
                return resourceManager.getResource(corrected);
            }
        };
        return resourceFactory;
    }

    public static PortalShader getPortalShader() {
        return portalShader;
    }
}
