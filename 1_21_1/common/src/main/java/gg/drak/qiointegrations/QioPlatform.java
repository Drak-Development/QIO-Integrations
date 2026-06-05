package gg.drak.qiointegrations;

import net.minecraft.world.level.block.entity.BlockEntity;

public final class QioPlatform {
    public interface Hooks {
        boolean isQioDashboard(BlockEntity blockEntity);

        boolean isModLoaded(String modId);
    }

    private static Hooks hooks = new Hooks() {
        @Override
        public boolean isQioDashboard(BlockEntity blockEntity) {
            return false;
        }

        @Override
        public boolean isModLoaded(String modId) {
            return false;
        }
    };

    private QioPlatform() {
    }

    public static void setHooks(Hooks newHooks) {
        hooks = newHooks;
    }

    public static boolean isQioDashboard(BlockEntity blockEntity) {
        return hooks.isQioDashboard(blockEntity);
    }

    public static boolean isModLoaded(String modId) {
        return hooks.isModLoaded(modId);
    }
}
