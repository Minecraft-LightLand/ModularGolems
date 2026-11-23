package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.client.armor.ShoulderSaddleModel;
import dev.xkmc.modulargolems.content.client.weapon.ShoulderAnimData;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 肩鞍：放在大型傀儡肩部，玩家/傀儡可以坐在上面，玩家可操控坐骑移动和大跳/冲锋。
 */
public class ShoulderSaddleItem extends ShouldWeaponItem {

	public ShoulderSaddleItem(Properties p) {
		super(p.stacksTo(1));
	}

	@Override
	public @Nullable ResourceLocation getModelForHand(InteractionHand hand) {
		// MAIN_HAND = 右肩槽，OFF_HAND = 左肩槽
		return hand == InteractionHand.MAIN_HAND ? ShoulderSaddleModel.SADDLE_RIGHT : ShoulderSaddleModel.SADDLE_LEFT;
	}

	@Override
	public @Nullable ResourceLocation getModelForHand(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		boolean right = entity.getRightShoulder().getItem().getItem() instanceof ShoulderSaddleItem;
		boolean left = entity.getLeftShoulder().getItem().getItem() instanceof ShoulderSaddleItem;
		if (right && left) {
			// 两侧都有鞍时只画一次整体模型，挂在右肩层上
			return hand == InteractionHand.MAIN_HAND ? ShoulderSaddleModel.SADDLE_FULL : null;
		}
		return hand == InteractionHand.MAIN_HAND ? ShoulderSaddleModel.SADDLE_RIGHT : ShoulderSaddleModel.SADDLE_LEFT;
	}

	@Override
	public List<ShoulderAnimData> getAnimationData(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return List.of();
	}

	@Override
	public ResourceLocation getModelTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		var id = ForgeRegistries.ITEMS.getKey(this);
		assert id != null;
		boolean right = entity.getRightShoulder().getItem().getItem() instanceof ShoulderSaddleItem;
		boolean left = entity.getLeftShoulder().getItem().getItem() instanceof ShoulderSaddleItem;
		// 两侧都有鞍时才用"全"纹理；只装一个时用左/右纹理，让另外半边保持透明、只显示单边
		String texture = right && left ? "shoulder_saddle_full"
				: hand == InteractionHand.MAIN_HAND ? "shoulder_saddle_left" : "shoulder_saddle_right";
		return id.withPath(e -> "textures/equipments/" + texture + ".png");
	}

	@Override
	public void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand) {
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		// 肩鞍只能装在大型傀儡（金属傀儡）肩上
		list.add(MGLangData.GOLEM_EQUIPMENT.get(GolemTypes.ENTITY_GOLEM.get().getDescription().copy().withStyle(ChatFormatting.GOLD))
				.withStyle(ChatFormatting.UNDERLINE));
		list.add(MGLangData.SHOULDER_SADDLE.get());
	}

}