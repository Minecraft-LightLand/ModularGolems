package dev.xkmc.modulargolems.compat.backpack;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.event.CurioEquipEvent;

/**
 * 饰品栏版本的空间背包绑定：傀儡通过 Curios 槽装备未绑定空间背包时，同样绑定到傀儡主人。
 * <p>
 * 单独成类是因为 Curios 是可选依赖，只有加载 Curios 时才会注册这个监听器。
 */
public class L2BCurioCompat {

	public static void register() {
		MinecraftForge.EVENT_BUS.register(L2BCurioCompat.class);
	}

	@SubscribeEvent
	public static void onCurioEquip(CurioEquipEvent event) {
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> golem) {
			L2BCompat.bindBackpack(golem, event.getStack());
		}
	}

	@SubscribeEvent
	public static void onCurioChange(CurioChangeEvent event) {
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> golem) {
			L2BCompat.bindBackpack(golem, event.getTo());
		}
	}

}
