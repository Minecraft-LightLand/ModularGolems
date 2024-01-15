package dev.xkmc.modulargolems.compat.curio;

import com.tterrag.registrate.util.entry.MenuEntry;
import dev.xkmc.l2tabs.compat.CuriosEventHandler;
import dev.xkmc.l2tabs.init.data.L2TabsLangData;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.registry.IMenuPvd;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

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

	public static IMenuPvd create(AbstractGolemEntity<?, ?> entity) {
		return new GolemCuriosMenuPvd(entity, 0);
	}

	public static void tryOpen(ServerPlayer player, LivingEntity target) {
		if (get() == null) return;
		var opt = CuriosApi.getCuriosInventory(target).resolve();
		if (opt.isEmpty()) return;
		if (opt.get().getSlots() == 0) return;
		var pvd = new GolemCuriosMenuPvd(target, 0);
		CuriosEventHandler.openMenuWrapped(player, () -> NetworkHooks.openScreen(player, pvd, pvd::writeBuffer));

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

	@Nullable
	public String getSkin(HumanoidGolemEntity le) {
		return CuriosApi.getCuriosInventory(le).resolve().flatMap(e -> e.getStacksHandler("golem_skin"))
				.map(ICurioStacksHandler::getStacks).map(e -> e.getSlots() == 0 ? null : e.getStackInSlot(0))
				.map(e -> e.getHoverName().getString()).orElse(null);
	}

}
