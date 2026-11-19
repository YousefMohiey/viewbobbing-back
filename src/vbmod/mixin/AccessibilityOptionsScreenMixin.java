package vbmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.Arrays;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Drops the "View Bobbing" entry from the Accessibility list. */
@Mixin(AccessibilityOptionsScreen.class)
public class AccessibilityOptionsScreenMixin {
    @ModifyReturnValue(method = "options", at = @At("RETURN"))
    private static OptionInstance<?>[] vbmod$removeBobbing(OptionInstance<?>[] original, Options options) {
        OptionInstance<?> bobbing = options.bobView();
        return Arrays.stream(original).filter(o -> o != bobbing).toArray(OptionInstance[]::new);
    }
}
