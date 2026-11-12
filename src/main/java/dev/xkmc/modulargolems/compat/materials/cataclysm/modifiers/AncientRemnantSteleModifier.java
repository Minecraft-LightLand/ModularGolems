package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.BiConsumer;

public class AncientRemnantSteleModifier extends GolemModifier {
	public static final int COOLDOWN = 200;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 25;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	public AncientRemnantSteleModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new AncientRemnantSteleAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	private static final ResourceLocation STELE = new ResourceLocation("cataclysm", "ancient_desert_stele");

	/**
	 * 远古金属头盔：岩碑落阵造成的伤害无视目标无敌帧。
	 */
	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (!CataArmorHelper.ancientHelmet(entity)) return;
		Entity direct = event.getSource().getDirectEntity();
		if (direct == null) return;
		if (!STELE.equals(ForgeRegistries.ENTITY_TYPES.getKey(direct.getType()))) return;
		event.getEntity().invulnerableTime = 0;
	}

	public static void addStele(LivingEntity user, LivingEntity target, int lv) {
		user.level().playLocalSound(user.getX(), user.getY(), user.getZ(), SoundEvents.RAVAGER_ROAR,
				user.getSoundSource(), 5.0F, 0.7F + user.getRandom().nextFloat() * 0.2F, false);
		float rot = user.getRandom().nextFloat() * Mth.TWO_PI;
		int branches = 8, perBranch = 16;
		double initialRadius = 2.0, radiusInc = 0.75, curve = 0.6;
		float angleInc = (float) (Math.PI * 2 / branches);
		double spawnY = user.getY() + 15;
		var center = user.position();
		for (int b = 0; b < branches; b++) {
			float baseAngle = angleInc * b + rot;
			for (int i = 0; i < perBranch; i++) {
				double radius = initialRadius + i * radiusInc;
				float angle = (float) (baseAngle + i * angleInc / initialRadius + i * curve);
				double x = center.x + radius * Math.cos(angle);
				double z = center.z + radius * Math.sin(angle);
				spawnAt(user, x, spawnY, z, 1 * (i + 1) + 10);
			}
		}
		float f = (float) Mth.atan2(target.getZ() - center.z, target.getX() - center.x);
		for (int l = 0; l < 16; l++) {
			double d2 = 1.25 * (l + 1);
			double x = center.x + Math.cos(f) * d2;
			double z = center.z + Math.sin(f) * d2;
			spawnAt(user, x, spawnY, z, (int) (5 + 1.5 * l) + 10);
		}
	}

	private static void spawnAt(LivingEntity user, double x, double y, double z, int delay) {
		GolemCataProxy.spawnDesertStele(user, x, y, z, delay);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH)).withStyle(ChatFormatting.GREEN));
	}

}
