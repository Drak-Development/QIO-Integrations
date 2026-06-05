package gg.drak.qiointegrations.neoforge;

import gg.drak.qiointegrations.QIOIntegrationsMod;
import gg.drak.qiointegrations.QioIntegrationsCore;
import gg.drak.qiointegrations.stub.QioIntegrationsStub;
import net.neoforged.fml.common.Mod;

@Mod(QIOIntegrationsMod.ID)
public class QIOIntegrationsNeoForge {
    public QIOIntegrationsNeoForge() {
        QioIntegrationsCore.class.getName();
        QioIntegrationsStub.markUnimplemented("neoforge", "1.21.11");
    }
}
