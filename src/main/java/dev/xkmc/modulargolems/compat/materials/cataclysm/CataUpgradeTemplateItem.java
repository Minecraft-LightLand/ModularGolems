package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class CataUpgradeTemplateItem extends Item {
    private static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    private static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
    private static final String DESCRIPTION_ID = Util.makeDescriptionId("item", new ResourceLocation("smithing_template"));
    private static final Component INGREDIENTS_TITLE = Component.translatable(
            Util.makeDescriptionId("item", new ResourceLocation("smithing_template.ingredients"))
    ).withStyle(TITLE_FORMAT);
    private static final Component APPLIES_TO_TITLE = Component.translatable(
            Util.makeDescriptionId("item", new ResourceLocation("smithing_template.applies_to"))
    ).withStyle(TITLE_FORMAT);

    private static final List<Consumer<RegistrateLangProvider>> LANG_REGISTRARS = List.of(
            pvd -> {
                pvd.add("item.modulargolems.harbinger_upgrade_template.desc", "Harbinger Upgrade");
                pvd.add("item.modulargolems.harbinger_upgrade_template.applies_to.desc", "Barbaric Vanguard Armor");
                pvd.add("item.modulargolems.harbinger_upgrade_template.ingredients.desc", "Witherite");
                pvd.add("item.modulargolems.monstrosity_upgrade_template.desc", "Monstrosity Upgrade");
                pvd.add("item.modulargolems.monstrosity_upgrade_template.applies_to.desc", "Barbaric Vanguard Armor");
                pvd.add("item.modulargolems.monstrosity_upgrade_template.ingredients.desc", "Monstrous Horn");
            }
    );

    public static void genLang(RegistrateLangProvider pvd) {
        for (var registrar : LANG_REGISTRARS) {
            registrar.accept(pvd);
        }
    }

    private final Component appliesTo;
    private final Component ingredients;
    private final Component upgradeDescription;

    public CataUpgradeTemplateItem(String upgradeName) {
        super(new Item.Properties());
        this.upgradeDescription = Component.translatable(
                "item.modulargolems." + upgradeName + "_upgrade_template.desc"
        ).withStyle(TITLE_FORMAT);
        this.appliesTo = Component.translatable(
                "item.modulargolems." + upgradeName + "_upgrade_template.applies_to.desc"
        ).withStyle(DESCRIPTION_FORMAT);
        this.ingredients = Component.translatable(
                "item.modulargolems." + upgradeName + "_upgrade_template.ingredients.desc"
        ).withStyle(DESCRIPTION_FORMAT);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(this.upgradeDescription);
        tooltip.add(CommonComponents.EMPTY);
        tooltip.add(APPLIES_TO_TITLE);
        tooltip.add(CommonComponents.space().append(this.appliesTo));
        tooltip.add(INGREDIENTS_TITLE);
        tooltip.add(CommonComponents.space().append(this.ingredients));
    }

    @Override
    public String getDescriptionId() {
        return DESCRIPTION_ID;
    }
}