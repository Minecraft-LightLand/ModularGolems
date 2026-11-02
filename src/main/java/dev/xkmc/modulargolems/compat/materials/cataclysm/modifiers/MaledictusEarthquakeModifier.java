package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.cataclysm_mux.MWCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.util.GolemUtils;
import dev.xkmc.modulargolems.util.SkillSound;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.function.Consumer;

public class MaledictusEarthquakeModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public MaledictusEarthquakeModifier() {
		super(StatFilterType.MOVEMENT, 1);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> cons) {
		cons.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (EarthquakeHelper.isLeaping(entity)) event.setAmount(event.getAmount() * EarthquakeHelper.LEAP_DAMAGE_MULT);
	}

	@Override
	public void handleEvent(AbstractGolemEntity<?, ?> golem, int value, byte event) {
		if (event == EarthquakeHelper.FLAG) {
			EarthquakeHelper.makeParticles(golem, 0, 0);
		}
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		int cd = 160;
		if (golem.getItemBySlot(EquipmentSlot.LEGS).is(CataCompatRegistry.MALEDICTUS_SHINGUARD.get()))
			cd /= 2;
		return cd;
	}

	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		var source = event.getSource();
		var direct = source.getDirectEntity();
		if (direct != null && direct.getType().builtInRegistryHolder().unwrapKey().orElseThrow().location()
				.equals(new ResourceLocation(CataDispatch.MODID, "phantom_halberd"))) {
			if (entity.getItemBySlot(EquipmentSlot.HEAD).is(CataCompatRegistry.MALEDICTUS_HELMET.get())) {
				event.getEntity().invulnerableTime = 0;
			}
		}
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return 25 * 25;
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		SkillSound.playQuakeOnly(golem, SoundEvents.GENERIC_EXPLODE, 1.5F, 1.0F + golem.getRandom().nextFloat() * 0.1F);
		for (LivingEntity entity : golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(7.0))) {
			if (GolemTargetUtil.canTarget(golem, entity)) {
				float damage = GolemUtils.adjustedDamage(
						(float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE),
						entity.getMaxHealth() * GolemCataProxy.maledictusEarthquakeDamage()
				);
				var src = golem.damageSources().mobAttack(golem);
				boolean flag = entity.hurt(src, damage);
				if (!flag) GolemCombatHelper.breakShield(golem, entity, src);
				if (flag) {
					EarthquakeHelper.launch(golem, entity, 0.5f);
				}
			}
		}
		Vec3 pos = findFloor(golem.level(), golem.position(), 8);
		if (pos == null) pos = golem.position();
		GolemCataProxy.screenShake(golem, 15F);
		MWCataProxy.spawnWindmillHalberd(golem, pos, 6, 10, 1.0F, 0.75F, 0.2F, 1);
		MWCataProxy.spawnWindmillHalberd(golem, pos, 4, 10, 1.0F, 1.2F, 0.15F, 1);
		MWCataProxy.spawnHalberdCircle(golem, pos, 14, 4.0F, 19);
		MWCataProxy.spawnHalberdCircle(golem, pos, 16, 5.5F, 20);
		MWCataProxy.spawnHalberdCircle(golem, pos, 18, 6.5F, 22);
		MWCataProxy.spawnHalberdCircle(golem, pos, 20, 7.5F, 23);
	}

	@org.jetbrains.annotations.Nullable
	private static Vec3 findFloor(Level level, Vec3 p, int diff) {
		double x = p.x, maxY = p.y, z = p.z, minY = maxY - diff;
		BlockPos pos = BlockPos.containing(x, maxY, z);
		double dy = 0.0F;

		do {
			BlockPos low = pos.below();
			BlockState lowState = level.getBlockState(low);
			if (lowState.isFaceSturdy(level, low, Direction.UP)) {
				if (!level.isEmptyBlock(pos)) {
					BlockState state = level.getBlockState(pos);
					VoxelShape shape = state.getCollisionShape(level, pos);
					if (!shape.isEmpty()) {
						dy = shape.max(Direction.Axis.Y);
					}
				}
				return new Vec3(x, pos.getY() + dy, z);
			}
			pos = pos.below();
		} while (pos.getY() >= minY);
		return null;
	}


	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", EarthquakeHelper.leapReduction()).withStyle(ChatFormatting.GREEN));
	}

}
