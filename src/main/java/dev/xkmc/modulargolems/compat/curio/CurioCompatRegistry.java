package dev.xkmc.modulargolems.compat.curio;

import com.tterrag.registrate.util.entry.MenuEntry;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2tabs.init.data.L2TabsLangData;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.registry.IMenuPvd;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CurioCompatRegistry {

	public static CurioCompatRegistry INSTANCE;

	@Nullable
	public static CurioCompatRegistry get() {
		if (ModList.get().isLoaded("curios")) {
			if (INSTANCE == null) {
				INSTANCE = new CurioCompatRegistry();
			}
			return INSTANCE;
		}
		return null;
	}

	public static void register() {
		var ins = get();
		if (ins == null) return;
		ins.registerImpl();
	}

	public static void clientRegister() {
		var ins = get();
		if (ins == null) return;
		ins.clientRegisterImpl();
	}

	public static <T> void onJEIRegistry(Consumer<Class<? extends ITabScreen>> consumer) {
		var ins = get();
		if (ins == null) return;
		ins.onJEIRegistryImpl(consumer);
	}

	public static void freezeMenu(AbstractGolemEntity<?, ?> golem) {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player.containerMenu instanceof GolemCuriosListMenu menu) {
			if (menu.curios.golem == golem) {
				menu.slots.clear();
			}
		}
	}

	public static IMenuPvd create(AbstractGolemEntity<?, ?> entity) {
		return new GolemCuriosMenuPvd(entity, 0);
	}

	public MenuEntry<GolemCuriosListMenu> menuType;
	public GolemTabToken<EquipmentGroup, GolemCurioTab> tab;

	public void registerImpl() {
		menuType = ModularGolems.REGISTRATE.menu("golem_curios", GolemCuriosListMenu::fromNetwork, () -> GolemCuriosListScreen::new).register();
	}

	public void clientRegisterImpl() {
		tab = new GolemTabToken<>(GolemCurioTab::new, () -> Items.AIR, L2TabsLangData.CURIOS.get());
		GolemTabRegistry.LIST_EQUIPMENT.add(tab);
	}

	private void onJEIRegistryImpl(Consumer<Class<? extends ITabScreen>> consumer) {
		consumer.accept(GolemCuriosListScreen.class);
	}

}
