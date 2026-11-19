package vbmod.legacy;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.Arrays;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_315;
import net.minecraft.class_4189;
import net.minecraft.class_7172;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/** 1.21.9 - 1.21.11: drops "View Bobbing" from the Accessibility list. */
@Pseudo
@Mixin(class_4189.class)
public class LegacyAccessibilityMixin {
    @ModifyReturnValue(method = "method_41839", at = @At("RETURN"))
    private static class_7172<?>[] vbmod$removeBobbing(class_7172<?>[] original, class_315 options) {
        // With Sodium installed the vanilla screen is never shown, so only drop the accessibility
        // entry once the Sodium hook has actually registered the option elsewhere.
        if (FabricLoader.getInstance().isModLoaded("sodium") && !VbState.sodiumHooked) {
            return original;
        }
        class_7172<?> bobbing = options.method_42448();
        return Arrays.stream(original).filter(o -> o != bobbing).toArray(class_7172[]::new);
    }
}
