package vbmod.legacy;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.caffeinemc.mods.sodium.client.gui.SodiumGameOptionPages;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpl;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.caffeinemc.mods.sodium.client.gui.options.control.TickBoxControl;
import net.caffeinemc.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_7172;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/** Sodium 0.7.x on 1.21.9/1.21.10: rebuilds the General page with the toggle appended. */
@Pseudo
@Mixin(SodiumGameOptionPages.class)
public class LegacySodiumPagesMixin {
    @ModifyReturnValue(method = "general", at = @At("RETURN"))
    private static OptionPage vbmod$addBobbing(OptionPage page) {
        VbState.sodiumHooked = true;
        class_315 options = class_310.method_1551().field_1690;
        class_7172<Boolean> bobbing = options.method_42448();

        OptionImpl<class_315, Boolean> option = OptionImpl
            .<class_315, Boolean>createBuilder(Boolean.class, new MinecraftOptionsStorage())
            .setName(class_2561.method_43471("options.viewBobbing"))
            .setTooltip(value -> class_2561.method_43470("Camera shake while walking. Same vanilla setting."))
            .setBinding((opts, value) -> bobbing.method_41748(value), opts -> bobbing.method_41753())
            .setControl(TickBoxControl::new)
            .build();

        OptionGroup group = OptionGroup.createBuilder().add(option).build();
        ImmutableList.Builder<OptionGroup> groups = ImmutableList.builder();
        groups.addAll(page.getGroups());
        groups.add(group);
        return new OptionPage(page.getName(), groups.build());
    }
}
