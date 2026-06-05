package gg.drak.qiointegrations.fabric;

import gg.drak.qiointegrations.QioIntegrationsCore;
import gg.drak.qiointegrations.stub.QioIntegrationsStub;
import net.fabricmc.api.DedicatedServerModInitializer;

public class QIOIntegrationsFabric implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        QioIntegrationsCore.class.getName();
        QioIntegrationsStub.markUnimplemented("fabric", "1.21.11");
    }
}
