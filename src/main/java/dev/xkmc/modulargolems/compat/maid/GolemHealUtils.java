package dev.xkmc.modulargolems.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.util.LinkedHashMap;
import java.util.Map;

public class GolemHealUtils {

	private final EntityMaid owner;
	private final CombinedInvWrapper inv;

	private final Map<ResourceLocation, Integer> cache = new LinkedHashMap<>();

	public GolemHealUtils(EntityMaid owner, CombinedInvWrapper inv) {
		this.owner = owner;
		this.inv = inv;
	}

	public boolean tryHealGolem(EntityMaid owner, AbstractGolemEntity<?, ?> golem) {
		if (!shouldHeal(golem)) return false;
		var opt = owner.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET);
		if (opt.isPresent()) {
			var target = opt.get();
			if (target instanceof AbstractGolemEntity<?, ?> old) {
				if (shouldHeal(old)) {
					return false;
				}
			}
		}
		if (owner.distanceTo(golem) < 2.5) {
			var mat = golem.getMaterials().get(MetalGolemPartType.BODY.ordinal()).id();
			var index = getIndexOfMaterial(mat);
			if (index >= 0) {
				if (!inv.extractItem(index, 1, false).isEmpty()) {
					golem.repairWithItem();
					float f1 = 1 + (golem.getRandom().nextFloat() - golem.getRandom().nextFloat()) * 0.2F;
					golem.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1, f1);
					owner.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
					return true;
				}
			}
		}
		owner.getBrain().setMemory(MemoryModuleType.INTERACTION_TARGET, golem);
		return false;
	}

	public void tryFixGolem() {
		for (int slot = 0; slot < inv.getSlots(); slot++) {
			var stack = inv.getStackInSlot(slot);
			if (stack.getItem() instanceof GolemHolder<?, ?> holder) {
				var mats = GolemHolder.getMaterial(stack);
				var type = holder.getEntityType();
				var part = type.getBodyPart();
				if (mats.size() <= part.ordinal()) continue;
				var mat = mats.get(part.ordinal()).id();
				int reforge = GolemHolder.getReforge(stack);
				if (reforge > 0) {
					var index = getIndexOfMaterial(mat);
					if (index >= 0) {
						if (!inv.extractItem(index, 1, false).isEmpty()) {
							GolemHolder.setReforge(stack, reforge - 1);
							inv.setStackInSlot(slot, stack);
						}
					}
					continue;
				}
				float max = GolemHolder.getMaxHealth(stack);
				float health = GolemHolder.getHealth(stack);
				if (health == -1) continue;
				if (health > max * 0.75) continue;
				var heal = holder.getInvHeal(stack, owner);
				if (heal > 0) continue;
				var index = getIndexOfMaterial(mat);
				if (index >= 0) {
					if (!inv.extractItem(index, 1, false).isEmpty()) {
						GolemHolder.setHealth(stack, Math.min(max, health + max / 4));
						inv.setStackInSlot(slot, stack);
					}
				}
			}
		}
	}

	private boolean shouldHeal(AbstractGolemEntity<?, ?> golem) {
		if (golem.getType() != GolemTypes.ENTITY_GOLEM.get())
			return false;
		if (golem.getHealth() > golem.getMaxHealth() * 0.75 && !golem.isReforged())
			return false;
		var mat = golem.getMaterials().get(MetalGolemPartType.BODY.ordinal()).id();
		return getIndexOfMaterial(mat) >= 0;
	}

	private int getIndexOfMaterial(ResourceLocation mat) {
		Ingredient ing = GolemMaterialConfig.get().getRepairIngredient(mat);
		var old = cache.get(mat);
		if (old != null) {
			if (old == -1) return -1;
			var item = inv.getStackInSlot(old);
			if (!item.isEmpty() && ing.test(item)) return old;
		}
		for (int i = 0; i < inv.getSlots(); i++) {
			var item = inv.getStackInSlot(i);
			if (!item.isEmpty() && ing.test(item)) {
				cache.put(mat, i);
				return i;
			}
		}
		cache.put(mat, -1);
		return -1;
	}

}
