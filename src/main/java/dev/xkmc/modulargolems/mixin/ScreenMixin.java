package dev.xkmc.modulargolems.mixin;

import dev.xkmc.modulargolems.content.menu.table.TableTab;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@Mixin(Screen.class)
public class ScreenMixin {

	@Shadow
	@Final
	public List<Renderable> renderables;

	@Inject(method = "rebuildWidgets", at = @At("HEAD"), require = 0)
	public void modulargolems$rebuildWidgets(CallbackInfo ci) {
		for (var e : renderables) {
			if (e instanceof TableTab tab) {
				tab.reinitIfMatched((Screen) (Object) this);
			}
		}
	}

}
