package gg.drak.qiointegrations;

import net.minecraft.network.chat.Component;

public enum QIOText {
    QIO_FREQUENCY("qio_frequency"),
    ;

    public final String key;

    QIOText(String key) {
        this.key = "text.%s.%s".formatted(QIOIntegrationsMod.ID, key);
    }

    public Component formatted(Object... params) {
        return Component.translatable(this.key, params);
    }
}
