package gg.drak.qiointegrations.forge;

import gg.drak.qiointegrations.QIOIntegrationsMod;
import gg.drak.qiointegrations.QioIntegrationsCore;
import gg.drak.qiointegrations.stub.QioIntegrationsStub;
import net.minecraftforge.fml.common.Mod;

@Mod(QIOIntegrationsMod.ID)
public class QIOIntegrationsForge {
    public QIOIntegrationsForge() {
        QioIntegrationsCore.class.getName();
        QioIntegrationsStub.markUnimplemented("forge", "1.21.11");
    }
}
