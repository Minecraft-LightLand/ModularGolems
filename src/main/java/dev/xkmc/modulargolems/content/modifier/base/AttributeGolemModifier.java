package dev.xkmc.modulargolems.content.modifier.base;

import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * should not be used in materials
 */
// 属性修饰器
public class AttributeGolemModifier extends GolemModifier {

	// AttrEntry 是一个内部记录类，用于存储属性类型和对应的属性值:
	// 一个 Supplier<golemstattype> 类型的对象，用于提供属性类型;一个 DoubleSupplier 类型的对象，用于提供属性值。
	public record AttrEntry(Supplier<GolemStatType> type, DoubleSupplier value) {
		// getValue 方法根据传入的等级 level 计算属性值，并将其乘以等级值，返回最终的属性值
		public double getValue(int level) {
			return value.getAsDouble() * level;
		}
	}

	// 用于存储多个 AttrEntry 对象，用于存储多个属性
	public final AttrEntry[] entries;

	// 构造方法
	public AttributeGolemModifier(int max, AttrEntry... entries) {
		super(StatFilterType.MASS, max);
		this.entries = entries;
	}

	@Override
	// 根据列表增加Tooltip
	public List<MutableComponent> getDetail(int v) {
		List<MutableComponent> ans = new ArrayList<>();
		for (AttrEntry ent : entries) {
			ans.add(ent.type.get().getAdderTooltip(ent.getValue(v)));
		}
		return ans;
	}

	@Nullable
	// 一个可空的 Set<golemstattype> 对象，用于存储当前属性修改器可能会与其他属性修改器冲突的属性类型
	private Set<GolemStatType> checking = null;

	// 如果 checking 字段为 null，则会创建一个新的 LinkedHashSet 对象
	private Set<GolemStatType> checking() {
		if (checking == null) {
			checking = new LinkedHashSet<>();
			// 将所有 AttrEntry 对象可能冲突的属性类型添加到 checking 集合中
			for (var e : entries) {
				checking.addAll(e.type.get().hasConflict());
			}
		}
		return checking;
	}

	@Override
	// 这个方法用于检查添加该属性修改器时是否会与其他属性修改器发生冲突。如果发生冲突，则返回一个非常小的整数值 -1000000，否则返回 0
	public int addSlot(List<IUpgradeItem> upgrades, int lv) {
		// 首先检查 checking 集合是否为空，如果为空，则说明没有冲突，返回 0
		if (checking().isEmpty()) return 0;
		// 如果不为空，则逐个检查传入的升级物品中是否包含与 checking 集合中相同的属性类型
		for (var item : upgrades) {
			if (item instanceof UpgradeItem up) {
				for (var mod : up.get()) {
					if (mod.mod() instanceof AttributeGolemModifier attr) {
						for (var ent : attr.entries) {
							var stat = ent.type().get();
							// 如果有冲突，则返回 -1000000
							if (checking().contains(stat)) return -1000000;
						}
					}
				}
			}
		}
		return 0;
	}

}
