package dev.xkmc.modulargolems.compat.materials.create;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.modulargolems.content.item.golem.GolemBEWLR;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.item.golem.IGolemPartItem;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class GolemIncompleteItem extends SequencedAssemblyItem implements IGolemPartItem {

	private final ItemEntry<?> item;

	public GolemIncompleteItem(Properties prop, ItemEntry<?> item) {
		super(prop);
		this.item = item;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(GolemBEWLR.EXTENSIONS);
	}

	@Override
	public GolemPart<?, ?> asPart() {
		return (GolemPart<?, ?>) item.get();
	}

}
