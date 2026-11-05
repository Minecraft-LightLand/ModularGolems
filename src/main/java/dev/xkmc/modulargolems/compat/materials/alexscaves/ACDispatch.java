package dev.xkmc.modulargolems.compat.materials.alexscaves;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.CandyCaneHookEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.SeekingArrowEntity;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.DinosaurSpiritEntity;
import com.github.alexmodguy.alexscaves.server.item.CandyCaneHookItem;
import com.github.alexmodguy.alexscaves.server.item.GalenaGauntletItem;
import com.github.alexmodguy.alexscaves.server.item.RaygunItem;
import com.github.alexmodguy.alexscaves.server.item.ResistorShieldItem;
import com.github.alexmodguy.alexscaves.server.message.UpdateItemTagMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import dev.xkmc.modulargolems.compat.materials.alexscaves.client.GolemPolarizeSound;
import dev.xkmc.modulargolems.compat.materials.alexscaves.client.MagneticWeaponLightningRenderer;
import dev.xkmc.modulargolems.compat.materials.alexscaves.client.RaygunBeamRenderer;
import dev.xkmc.modulargolems.compat.materials.alexscaves.modifier.PolarizeModifier;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.impl.GolemUseItemHook;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.items.IItemHandler;

import java.util.UUID;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.events.event.GolemEquipItemEvent;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ACDispatch extends ModDispatch {

	public static final String MODID = "alexscaves";

	@SubscribeEvent
	public static void onGolemEquip(GolemEquipItemEvent event) {
		if (event.getEntity().getModifiers().containsKey(ACCompatRegistry.ATOMIC.get())) {
			if (ACCompatRegistry.DUMMY_URANIUM.get().isValid(event.getStack())) {
				event.setSlot(event.getStack().getCount(), EquipmentSlot.OFFHAND);
			}
		}
		if (event.getEntity().getModifiers().containsKey(ACCompatRegistry.REFORMATION.get())) {
			if (ACCompatRegistry.DUMMY_IRON.get().isValid(event.getStack())) {
				event.setSlot(event.getStack().getCount(), EquipmentSlot.OFFHAND);
			}
		}
	}

	/** 正在开火的傀儡 id，用来只播一次"开始使用"音效。 */
	private static final java.util.Set<Integer> RAYGUN_ACTIVE = new java.util.HashSet<>();
	/**
	 * 让渲染用的 {@code UseTime} 与 {@code PrevUseTime} 始终相等。
	 * <p>
	 * 护手的展开动画（{@code ACItemstackRenderer} 第 132 行）和射线枪的枪身动画都读
	 * {@code getLerpedUseTime(stack, partialTicks) = prev + partialTicks * (current - prev)}。
	 * 只要 {@code prev != current}，这个值就会<b>随渲染帧率在两者之间来回摆</b>，
	 * 表现为模型/手臂持续高频抖动——这不是模型问题，是 tag 里两个字段不同步。
	 * <p>
	 * 原版玩家每 tick 走 {@code inventoryTick}，{@code PrevUseTime} 会被自动对齐成上一 tick 的值，
	 * 所以摆动幅度只有 1 且方向一致，看起来是平滑展开；傀儡的装备槽不走背包 tick，
	 * 没人做这个对齐，两个值可能差很多（例如 5 与 0），摆幅就很大。
	 * <p>
	 * 这里在<b>使用中和空闲</b>两种状态下都把 {@code PrevUseTime} 写成与 {@code UseTime} 相同：
	 * 插值恒等于当前值，彻底不摆；展开进度仍按"使用中 +1 / 松手 -1"推进，节奏不变。
	 * 只改<b>客户端</b>那份 ItemStack 的 tag、不联网，服务端 tag 保持静止，
	 * {@code detectEquipmentUpdates} 不会发包覆盖。
	 */
	/**
	 * 射线枪空闲时把渲染用的 {@code UseTime}/{@code PrevUseTime} 一起收回。
	 * <p>
	 * 方铅护手<b>不</b>在这里处理：它的 {@code UseTime} 由服务端 {@code tickGauntletUseTime}
	 * 单独推进，客户端只读同步下来的值。多个写入者会让 {@code UseTime} 在同步包与本地推进之间
	 * 反复跳（实测出现 0↔2 的逐帧翻转），而 {@code GalenaGauntletModel} 的手指动画幅度
	 * 正是 {@code openAmount = UseTime/5}，幅度乱跳 = 模型抖动。
	 */
	/**
	 * 射线枪的蓄力进度 {@code UseTime}：<b>唯一写入者</b>，并且每次都把
	 * {@code PrevUseTime} 对齐成同一个值。
	 * <p>
	 * 原版玩家靠 {@code RaygunItem#inventoryTick} 的客户端分支推进（使用中 +1、松手 -1），
	 * 而 {@code setUseTime} 会把 {@code PrevUseTime} 写成<b>调用前</b>的值——于是两个字段
	 * 始终差 1，渲染插值 {@code getLerpedUseTime = prev + partialTicks * (cur - prev)}
	 * 每帧在两者之间摆一次。玩家看不出来是因为摆幅只有 1/5，且只在开火期间发生。
	 * <p>
	 * 傀儡这边如果同时保留 {@code inventoryTick} 的推进和本方法的对齐，两个写入者会互相
	 * 覆盖：松手后 {@code UseTime} 被本方法递减到 0，但 {@code PrevUseTime} 停在
	 * {@code inventoryTick} 最后写入的旧值，插值恒不为 0 →
	 * {@code pulseAlpha = useAmount * (...)} 一直非零，枪身的发光贴图在<b>不使用时也在闪</b>。
	 * <p>
	 * 所以射线枪也走"单写入者 + 每 tick 两值对齐"：使用中推到 5、空闲收回 0，
	 * {@code prev} 永远等于 {@code cur}，插值恒等，既不会闪也不会抖。
	 * <p>
	 * 方铅护手<b>不</b>在这里处理：它的 {@code UseTime} 由服务端 {@code tickGauntletUseTime}
	 * 独占推进，客户端只读同步值（多个写入者会让它在同步包与本地推进之间 0↔2 翻转）。
	 */
	private static void settleRenderedUseTime(AbstractGolemEntity<?, ?> golem) {
		if (!golem.level().isClientSide) return;
		boolean using = golem.isUsingItem();
		for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}) {
			ItemStack stack = golem.getItemBySlot(slot);
			if (!(stack.getItem() instanceof RaygunItem)) continue;
			CompoundTag tag = stack.getTag();
			if (tag == null) continue;
			int cur = tag.getInt("UseTime");
			int next = using ? Math.min(5, cur + 1) : Math.max(0, cur - 1);
			tag.putInt("UseTime", next);
			tag.putInt("PrevUseTime", next);
		}
	}
	/**
	 * 射线枪的充能（{@code ChargeUsed}）、蓄力进度（{@code UseTime}）和光束终点（{@code RayX/Y/Z}）
	 * 全部只写在<b>客户端</b>那份 ItemStack 的 tag 里（{@code RaygunItem} 的 {@code onUseTick} /
	 * {@code inventoryTick} 客户端分支）。原版玩家靠松手时的 {@code releaseUsing} 把它们发回服务端。
	 * <p>
	 * 傀儡不松手就一直用下去，服务端那份 tag 永远停在旧值；而装备每 tick 都参与
	 * {@code Mob#detectEquipmentUpdates} 的比较，一旦两边不一致，同步包就会把客户端改过的 tag
	 * 整个覆盖回旧值——表现为「充能涨一下又变回去」「光束永远不出现」（{@code UseTime} 到不了 5、
	 * {@code RayX/Y/Z} 被抹掉）。
	 * <p>
	 * 这里在客户端每 tick 把 tag 推回服务端，让两边始终一致，同步就不再覆盖任何东西。
	 * 只在「正在使用 + 手里是射线枪」时发包，开销可忽略。
	 */
	private static void syncUsingRaygunTag(AbstractGolemEntity<?, ?> golem) {
		settleRenderedUseTime(golem);
		if (!golem.level().isClientSide || !golem.isUsingItem()) {
			RAYGUN_ACTIVE.remove(golem.getId());
			return;
		}
		ItemStack use = golem.getUseItem();
		if (use.isEmpty()) {
			RAYGUN_ACTIVE.remove(golem.getId());
			return;
		}
		if (!(use.getItem() instanceof RaygunItem)) {
			RAYGUN_ACTIVE.remove(golem.getId());
			return;
		}
		if (RAYGUN_ACTIVE.add(golem.getId())) {
			// 原版在 RaygunItem#use 里播这个音效，傀儡不走 use()，这里补一次。
			golem.level().playLocalSound(golem.getX(), golem.getY(), golem.getZ(),
					ACSoundRegistry.RAYGUN_START.get(), SoundSource.PLAYERS, 1.0F, 1.0F, false);
		}
		// UseTime 的推进已统一交给开头的 {@code settleRenderedUseTime}（它同时把
		// {@code PrevUseTime} 对齐）。这里不能再调 {@code setUseTime}：那会把
		// {@code PrevUseTime} 写回旧值，两个字段重新拉开 → 枪身发光闪烁 + 模型抖动。
		AlexsCaves.sendMSGToServer(new UpdateItemTagMessage(golem.getId(), use));
	}

	/**
	 * 射线枪充能耗尽后用铀棒装填，等价于原版 {@code RaygunItem#use} 里的换弹分支
	 * （那条只接受 {@code Player} 且从玩家背包找弹药，傀儡走不到）。
	 * <p>
	 * 查找方式和口香糖枪一致：走物品处理器能力而不是只看双手——傀儡的
	 * {@code CombinedInvWrapper} 把双手、装备槽和<b>专门的弹药槽</b>都聚合进同一个
	 * {@code IItemHandler}，这样放在弹药槽里的铀棒也能被找到。
	 */
	private static void reloadRaygun(AbstractGolemEntity<?, ?> golem) {
		ItemStack main = golem.getItemBySlot(EquipmentSlot.MAINHAND);
		if (!(main.getItem() instanceof RaygunItem)) return;
		if (RaygunItem.hasCharge(main)) return;
		var handler = golem.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (handler.isEmpty()) return;
		IItemHandler inv = handler.get();
		for (int slot = 0; slot < inv.getSlots(); slot++) {
			ItemStack s = inv.getStackInSlot(slot);
			if (s.isEmpty() || !RaygunItem.AMMO.test(s)) continue;
			// 用 extractItem 而不是 s.shrink(1)：capability 返回的不一定是可写回的栈
			// （CombinedInvWrapper 之类返回副本），shrink 只改副本 → 弹药永远扣不掉 →
			// hasAmmo 恒为真 → checkSwitch 反复切手。口香糖枪是同一个坑。
			inv.extractItem(slot, 1, false);
			RaygunItem.setCharge(main, 0);
			golem.level().playSound(null, golem.getX(), golem.getY(), golem.getZ(),
					ACSoundRegistry.RAYGUN_RELOAD.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			return;
		}
	}

	/**
	 * 阻尼盾的循环音效。
	 * <p>
	 * 原版由 {@code ResistorShieldItem#onUseTick} 里的 {@code playWorldSound(living, 9/10)} 驱动，
	 * 但那条链路依赖 AC 的 {@code ItemTickableSound}——它要求实体"正在使用物品"，而傀儡的极化
	 * 是 MG 复刻的，实体并不处于使用中，音效永远播不出来。
	 * <p>
	 * 所以直接复用磁力极化那套 {@link GolemPolarizeSound}：独立维护一条循环音，按红/蓝极性切换。
	 */
	private static final java.util.Map<Integer, GolemPolarizeSound> SHIELD_SOUNDS = new java.util.HashMap<>();

	private static void playShieldSound(AbstractGolemEntity<?, ?> golem) {
		ItemStack shield = ItemStack.EMPTY;
		for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}) {
			ItemStack s = golem.getItemBySlot(slot);
			if (s.getItem() instanceof ResistorShieldItem) {
				shield = s;
				break;
			}
		}
		if (shield.isEmpty() || !golem.isAggressive()) {
			GolemPolarizeSound old = SHIELD_SOUNDS.remove(golem.getId());
			if (old != null) old.stopSound();
			return;
		}
		boolean azure = !ResistorShieldItem.isScarlet(shield);
		GolemPolarizeSound sound = SHIELD_SOUNDS.get(golem.getId());
		if (sound != null && sound.isAzure() == azure) return;
		if (sound != null) sound.stopSound();
		GolemPolarizeSound created = new GolemPolarizeSound(golem, azure, g -> g.isAggressive() && hasShield(g));
		SHIELD_SOUNDS.put(golem.getId(), created);
		net.minecraft.client.Minecraft.getInstance().getSoundManager().play(created);
	}

	private static boolean hasShield(AbstractGolemEntity<?, ?> golem) {
		return golem.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof ResistorShieldItem
				|| golem.getItemBySlot(EquipmentSlot.OFFHAND).getItem() instanceof ResistorShieldItem;
	}

	/**
	 * 阻尼盾的 {@code ARROW_INDUCTING} 附魔：格挡箭矢时把它变成追踪箭反弹回去。
	 * <p>
	 * 原版在 {@code CommonEvents#livingAttack} 里实现，条件写死了 {@code event.getEntity() instanceof Player}
	 * 且 {@code isBlocking()} —— 傀儡不格挡、也不是 {@code Player}，这段永远不执行。
	 * <p>
	 * 傀儡的"格挡"是 MG 自己复刻的极化效果，没有 vanilla 的 blocking 状态，所以这里放宽成
	 * "主手或副手持有带该附魔的阻尼盾"，效果与原版一致。
	 */
	@SubscribeEvent
	public static void onGolemArrowBlocked(LivingAttackEvent event) {
		if (!(event.getEntity() instanceof AbstractGolemEntity<?, ?> golem)) return;
		if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) return;
		ItemStack shield = findShield(golem);
		if (shield.isEmpty()) return;
		if (shield.getEnchantmentLevel(ACEnchantmentRegistry.ARROW_INDUCTING.get()) <= 0) return;
		if (arrow.getType() == ACEntityRegistry.SEEKING_ARROW.get()) return;
		SeekingArrowEntity seeking = new SeekingArrowEntity(golem.level(), golem);
		seeking.copyPosition(arrow);
		seeking.setDeltaMovement(arrow.getDeltaMovement().scale(-0.4D));
		seeking.setYRot(arrow.getYRot() + 180.0F);
		golem.level().addFreshEntity(seeking);
		arrow.discard();
	}

	/** 找到傀儡身上带附魔判定用的阻尼盾（主手或副手）。 */
	private static ItemStack findShield(AbstractGolemEntity<?, ?> golem) {
		ItemStack main = golem.getItemBySlot(EquipmentSlot.MAINHAND);
		if (main.getItem() instanceof ResistorShieldItem) return main;
		ItemStack off = golem.getItemBySlot(EquipmentSlot.OFFHAND);
		return off.getItem() instanceof ResistorShieldItem ? off : ItemStack.EMPTY;
	}
	/**
	 * 拐杖糖钩锁：钩子消失后清理物品 NBT 里的 {@code LastLaunchedHookUUID}。
	 * <p>
	 * {@code CandyCaneHookItem#isActive()} 只看这个 UUID 存不存在，所以不清的话物品会永远停在
	 * "已扔出"状态。玩家版靠 {@code inventoryTick} 里"物品不在手上就收线"那条自动清理，
	 * 傀儡的装备槽不走背包 tick，必须自己补。
	 * <p>
	 * 必须每 tick 对所有槽位跑一遍，而不是只在 {@code trigger()} 里清：傀儡换到近战武器后
	 * 钩锁的 behavior 根本不会被调用，残留就永远清不掉。
	 */
	private static void cleanupHookNbt(AbstractGolemEntity<?, ?> golem) {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = golem.getItemBySlot(slot);
			if (!(stack.getItem() instanceof CandyCaneHookItem)) continue;
			if (!CandyCaneHookItem.isActive(stack)) continue;
			UUID uuid = CandyCaneHookItem.getLaunchedHookUUID(stack);
			Entity hook = uuid != null && golem.level() instanceof ServerLevel serverLevel
					? serverLevel.getEntity(uuid) : null;
			// 钩子已经不在了（消失 / 被回收 / 击杀后消散）→ 清掉 NBT
			if (hook instanceof CandyCaneHookEntity caneHook && caneHook.isAlive()) continue;
			CandyCaneHookItem.setLastLaunchedHookUUID(stack, null);
			CandyCaneHookItem.setReelingIn(stack, false);
		}
	}
	/**
	 * 灭绝长矛蓄力时的"洞穴角龙灵魂护盾"。
	 * <p>
	 * 原版在 {@code CommonEvents#livingHurt} 里实现：{@code event.getEntity() instanceof Player} 且手持
	 * 长矛时，消耗最近的一个 {@code GROTTOCERATOPS} 龙灵并取消伤害。傀儡不是 {@code Player}，
	 * 条件直接跳过，于是蓄力期间完全吃伤害。这里按同样的规则补一份。
	 */
	@SubscribeEvent
	public static void onGolemHurt(LivingDamageEvent event) {
		if (!(event.getEntity() instanceof AbstractGolemEntity<?, ?> golem)) return;
		if (!golem.getUseItem().is(ACItemRegistry.EXTINCTION_SPEAR.get())) return;
		if (!consumeGrottoGhost(golem)) return;
		event.setCanceled(true);
		golem.level().playSound(null, golem.getX(), golem.getY(), golem.getZ(),
				SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
	}

	/** 消耗离傀儡最近的一个蓄力龙灵；没有可消耗的返回 false。 */
	private static boolean consumeGrottoGhost(AbstractGolemEntity<?, ?> golem) {
		DinosaurSpiritEntity closest = null;
		for (DinosaurSpiritEntity spirit : golem.level().getEntitiesOfClass(DinosaurSpiritEntity.class,
				golem.getBoundingBox().inflate(30, 30, 30))) {
			if (golem.getUUID().equals(spirit.getPlayerUUID())
					&& spirit.getDinosaurType() == DinosaurSpiritEntity.DinosaurType.GROTTOCERATOPS
					&& !spirit.isFading()) {
				if (closest == null || closest.distanceTo(golem) > spirit.distanceTo(golem)) {
					closest = spirit;
				}
			}
		}
		if (closest == null) return false;
		closest.setFading(true);
		return true;
	}

	@SubscribeEvent
	public static void onGolemAiStep(LivingEvent.LivingTickEvent event) {
		if (!(event.getEntity() instanceof AbstractGolemEntity<?, ?> golem)) return;
		if (!golem.level().isClientSide) {
			reloadRaygun(golem);
			cleanupHookNbt(golem);
			tickGauntletUseTime(golem);
		}
		// 主手、副手都算：主手举盾同样能格挡，没理由只认副手。
		boolean holdingShield = golem.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof ResistorShieldItem
				|| golem.getItemBySlot(EquipmentSlot.OFFHAND).getItem() instanceof ResistorShieldItem;
		if (!holdingShield) return;
		// 已经有极化 modifier 的傀儡不重复执行（它自己的 onClientTick / onAiStep 会跑）
		if (golem.getModifiers().containsKey(ACCompatRegistry.POLARIZE.get())) return;
		if (golem.level().isClientSide) {
			// 粒子（红/蓝护盾闪电）只写在 PolarizeModifier#onClientTick 里，服务端跑不到
			playShieldSound(golem);
			PolarizeModifier.runOnceClient(golem, 1);
			return;
		}
		if (!golem.isAggressive()) return;
		PolarizeModifier.runOnce(golem, 1);
	}

	/**
	 * 服务端推进方铅护手的展开进度（{@code UseTime} 0→5 / 5→0）。
	 * <p>
	 * <b>为什么必须是服务端独占</b>：装备每 tick 参与 {@code Mob#detectEquipmentUpdates} 的比较，
	 * 服务端那份 tag 才是"真值"。客户端若自己推进，服务端就会判定不一致并每 tick 发包覆盖，
	 * 客户端 {@code UseTime} 于是在"本地推进值"和"服务端旧值"之间逐帧翻转
	 * （实测 0↔2），手指动画幅度 {@code openAmount = UseTime/5} 跟着乱跳 → 模型抖动。
	 * <p>
	 * 用 {@code setUseTime} 而不是直接 {@code putInt}：它会把 {@code PrevUseTime} 写成调用前的值，
	 * 展开过程因此只跨 1 个单位（平滑），到达端点后两值相等（插值恒等、不再摆）。
	 */
	private static void tickGauntletUseTime(AbstractGolemEntity<?, ?> golem) {
		for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}) {
			ItemStack held = golem.getItemBySlot(slot);
			if (!(held.getItem() instanceof GalenaGauntletItem)) continue;
			int useTime = GalenaGauntletItem.getUseTime(held);
			boolean inUse = golem.isUsingItem() && (slot == EquipmentSlot.MAINHAND
					? golem.getUsedItemHand() == InteractionHand.MAIN_HAND
					: golem.getUsedItemHand() == InteractionHand.OFF_HAND);
			int next = inUse ? Math.min(5, useTime + 1) : Math.max(0, useTime - 1);
			// <b>必须每 tick 无条件调用</b>，即使 {@code next} 与当前值相同。
			// {@code setUseTime} 会把 {@code PrevUseTime} 写成调用前的值，所以
			// "到 5 后不再调用"会让 {@code PrevUseTime} 永远停在 4，
			// 而渲染插值 {@code getLerpedUseTime = prev + partialTicks * (cur - prev)}
			// 于是每帧在 0.8~1.0 之间摆动；{@code GalenaGauntletModel} 的手指动画幅度
			// 正是 {@code openAmount = UseTime/5}，幅度每帧变 → 手指/模型高频抖动。
			// 每 tick 都调用时，到达端点后 prev 与 cur 相等，插值恒等，抖动消失。
			GalenaGauntletItem.setUseTime(held, next);
		}
	}
	public ACDispatch() {
		ACCompatRegistry.register();
		MinecraftForge.EVENT_BUS.register(ACDispatch.class);
		// 必须挂在 tickClientUseItem 之后：挂 tick 开头会拿到上一 tick 的 tag，被服务端同步拉回去形成死循环
		GolemUseItemHook.POST_TICK = ACDispatch::syncUsingRaygunTag;
				// 第一人称补画傀儡射线枪光束：AC 的 postRenderLiving 只在非第一人称时画其它实体
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> MagneticWeaponLightningRenderer::register);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> RaygunBeamRenderer::register);
	}
	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".candy", "Candy");
		pvd.add("golem_material." + MODID + ".magnetic", "Magnetic");
		pvd.add("golem_material." + MODID + ".nuclear", "Nuclear");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {


		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC,
								ACCompatRegistry.REPAIR_CANDY.get())::unlockedBy,
						ACItemRegistry.CARAMEL.get())
				.pattern("BBB").pattern("CDC").pattern("AAA")
				.define('A', ACBlockRegistry.FROSTED_GINGERBREAD_BLOCK.get())
				.define('B', ACBlockRegistry.CANDY_CANE_BLOCK.get())
				.define('C', ACItemRegistry.CARAMEL.get())
				.define('D', ACBlockRegistry.LICOROOT.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC,
								ACCompatRegistry.CRAFT_CANDY.get(), 3)::unlockedBy,
						ACItemRegistry.RADIANT_ESSENCE.get().asItem())
				.pattern("GBG").pattern("ZCZ").pattern("AAA")
				.define('A', ACCompatRegistry.REPAIR_CANDY.get())
				.define('B', ACItemRegistry.GUMBALL_PILE.get())
				.define('G', ACBlockRegistry.SUGAR_GLASS.get())
				.define('Z', ACItemRegistry.HOT_CHOCOLATE_BOTTLE.get())
				.define('C', ACItemRegistry.RADIANT_ESSENCE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC,
								ACCompatRegistry.REPAIR_MAGNETIC.get())::unlockedBy,
						ACItemRegistry.SCARLET_NEODYMIUM_INGOT.get())
				.pattern("SBC").pattern("BCB").pattern("CBA")
				.define('A', ACItemRegistry.AZURE_NEODYMIUM_INGOT.get())
				.define('S', ACItemRegistry.SCARLET_NEODYMIUM_INGOT.get())
				.define('B', ACItemRegistry.FERROUSLIME_BALL.get())
				.define('C', ACBlockRegistry.PACKED_GALENA.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC,
								ACCompatRegistry.CRAFT_MAGNETIC.get(), 3)::unlockedBy,
						ACBlockRegistry.HEART_OF_IRON.get().asItem())
				.pattern("ANA").pattern("TCT").pattern("ANA")
				.define('A', ACCompatRegistry.REPAIR_MAGNETIC.get())
				.define('T', ACItemRegistry.TELECORE.get())
				.define('N', ACItemRegistry.NOTOR_COMPONENT.get())
				.define('C', ACBlockRegistry.HEART_OF_IRON.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC,
								ACCompatRegistry.CRAFT_NUCLEAR.get(), 3)::unlockedBy,
						ACBlockRegistry.HEART_OF_IRON.get().asItem())
				.pattern("AAA").pattern("CDC").pattern("BBB")
				.define('A', ACItemRegistry.POLYMER_PLATE.get())
				.define('B', ACItemRegistry.CHARRED_REMNANT.get())
				.define('C', ACBlockRegistry.URANIUM_ROD.get())
				.define('D', ACItemRegistry.FISSILE_CORE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, new ShapelessRecipeBuilder(RecipeCategory.MISC,
						ACCompatRegistry.UPGRADE_ATOMIC_BREATH.get(), 1)::unlockedBy,
						ACBlockRegistry.NUCLEAR_BOMB.get().asItem())
				.requires(GolemItems.EMPTY_UPGRADE.get())
				.requires(ACBlockRegistry.TREMORZILLA_EGG.get().asItem())
				.requires(ACBlockRegistry.NUCLEAR_BOMB.get().asItem())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));
	}

	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new ACConfigGen(gen);
	}

}
