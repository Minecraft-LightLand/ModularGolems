package dev.xkmc.modulargolems.content.block;

import dev.xkmc.modulargolems.content.menu.table.SimpleUpgradeMenu;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class SimpleTableBlock extends TableBlock {

	public SimpleTableBlock(Properties prop) {
		super(prop);
	}

	@Override
	public MenuProvider getMenuProvider(BlockState state, net.minecraft.world.level.Level level, BlockPos pos) {
		return new SimpleMenuProvider((wid, inv, player) ->
				new SimpleUpgradeMenu(GolemMiscs.SIMPLE_UPGRADE.get(), wid, inv), MGLangData.TAB_UPGRADES.get());
	}

}