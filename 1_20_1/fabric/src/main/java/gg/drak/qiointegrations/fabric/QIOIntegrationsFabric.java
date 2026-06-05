package gg.drak.qiointegrations.fabric;

import gg.drak.qiointegrations.stub.QioIntegrationsStub;
import net.fabricmc.api.DedicatedServerModInitializer;

public class QIOIntegrationsFabric implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        QioIntegrationsStub.markUnimplemented("fabric", "1.20.1");
    }
}
