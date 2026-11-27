package dev.xkmc.modulargolems.content.item.equipments.realm;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.IAttackListenerWeapon;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemWeaponItem;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 戍卫战斧（界域联动，大型傀儡专属）。属性同下界合金战斧额外 +60% 攻击伤害，并附带「界域戍卫」特性：
 * 近战伤害穿透护甲；对受到感电效果的目标造成更高伤害。特性只写在本武器代码里，且要求傀儡拥有界域部件（eeeabsmobs:realm）才生效。
 */
public class RealmWardenAxe extends MetalGolemWeaponItem implements IAttackListenerWeapon {

	/** 界域材质 id（eeeabsmobs:realm）。用字面量，避免核心类引用可选 mod 的类。 */
	private static final ResourceLocation REALM_MATERIAL = new ResourceLocation("eeeabsmobs", "realm");

	public RealmWardenAxe(Properties properties) {
		super(properties, 0, 0.6, 0, 2);
	}

	@Override
	public void onCreateSource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, ItemStack stack) {
		if (!hasRealmPart(golem)) return;
		if (event.getResult() == null) return;
		if (!event.getResult().validState(DefaultDamageState.BYPASS_ARMOR)) return;
		event.enable(DefaultDamageState.BYPASS_ARMOR);
	}

	@Override
	public void onHurt(AttackCache cache, DamageSource source, MetalGolemEntity e, ItemStack stack) {
		if (!hasRealmPart(e)) return;
		var target = cache.getAttackTarget();
		if (target == null || !isElectrified(target)) return;
		cache.addHurtModifier(DamageModifier.multTotal(1.3F));
	}

	/** 是否拥有界域部件（照音波炮判定幽匿部件的写法：遍历傀儡材质）。 */
	private static boolean hasRealmPart(AbstractGolemEntity<?, ?> golem) {
		for (var m : golem.getMaterials()) {
			if (m.id().equals(REALM_MATERIAL)) return true;
		}
		return false;
	}

	private static boolean isElectrified(LivingEntity target) {
		for (MobEffectInstance ins : target.getActiveEffects()) {
			ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(ins.getEffect());
			if (id != null && id.getNamespace().equals("eeeabsmobs") && id.getPath().contains("electr")) return true;
		}
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		list.add(MGLangData.REALM_WARDEN.get());
	}

}
