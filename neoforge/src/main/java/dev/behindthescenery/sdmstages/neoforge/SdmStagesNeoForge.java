package dev.behindthescenery.sdmstages.neoforge;

import dev.behindthescenery.sdmstages.SdmStages;
import net.neoforged.fml.common.Mod;

@Mod(SdmStages.MODID)
public final class SdmStagesNeoForge {
    public SdmStagesNeoForge() {
        // Run our common setup.
        SdmStages.init(new NeoForgeNetworkHandler());
    }
}
