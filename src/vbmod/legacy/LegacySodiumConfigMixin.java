package vbmod.legacy;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.caffeinemc.mods.sodium.api.config.structure.BooleanOptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.caffeinemc.mods.sodium.client.gui.SodiumConfigBuilder;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_7172;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/** Sodium 0.8+ on 1.21.x: adds the toggle to Sodium's General page. */
@Pseudo
@Mixin(SodiumConfigBuilder.class)
public class LegacySodiumConfigMixin {
    @ModifyReturnValue(method = "buildGeneralPage", at = @At("RETURN"))
    private OptionPageBuilder vbmod$addBobbing(OptionPageBuilder page, ConfigBuilder builder) {
        VbState.sodiumHooked = true;
        class_315 options = class_310.method_1551().field_1690;
        class_7172<Boolean> bobbing = options.method_42448();

        BooleanOptionBuilder option = builder
            .createBooleanOption(class_2960.method_60655("viewbobbingback", "view_bobbing"))
            .setName(class_2561.method_43471("options.viewBobbing"))
            .setTooltip(class_2561.method_43470("Camera shake while walking. Same vanilla setting."))
            .setStorageHandler(options::method_1640)
            .setBinding(value -> bobbing.method_41748(value), () -> bobbing.method_41753())
            .setDefaultValue(true);

        page.addOptionGroup(builder.createOptionGroup().addOption(option));
        return page;
    }
}
