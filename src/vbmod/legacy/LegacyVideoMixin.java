package vbmod.legacy;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.Arrays;
import net.minecraft.class_315;
import net.minecraft.class_446;
import net.minecraft.class_7172;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/** 1.21.9 - 1.21.11: puts "View Bobbing" in the first row of Video Settings, right column. */
@Pseudo
@Mixin(class_446.class)
public class LegacyVideoMixin {
    /** 1.21.9 and 1.21.10 build the whole list in one method. */
    @ModifyReturnValue(method = "method_41846", at = @At("RETURN"))
    private static class_7172<?>[] vbmod$insertOld(class_7172<?>[] original, class_315 options) {
        return insert(original, options);
    }

    /** 1.21.11 split the list into display/quality/preferences; this one is the display row. */
    @ModifyReturnValue(method = "method_75373", at = @At("RETURN"))
    private static class_7172<?>[] vbmod$insertNew(class_7172<?>[] original, class_315 options) {
        return insert(original, options);
    }

    private static class_7172<?>[] insert(class_7172<?>[] original, class_315 options) {
        class_7172<?>[] out = Arrays.copyOf(original, original.length + 1);
        out[0] = original[0];
        out[1] = options.method_42448();
        System.arraycopy(original, 1, out, 2, original.length - 1);
        return out;
    }
}
