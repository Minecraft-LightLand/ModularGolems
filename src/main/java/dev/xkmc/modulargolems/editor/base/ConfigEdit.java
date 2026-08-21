package dev.xkmc.modulargolems.editor.base;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Project-agnostic read/write access to the Forge config for the editor. Subclasses supply the
 * config specs, lang key prefix and home sections of the embedding mod. Reusable by other mods
 * built on the l2 config system.
 */
public abstract class ConfigEdit {

	public enum Kind {BOOL, INT, DOUBLE, STRING}

	/**
	 * One config value together with the widgets to edit it.
	 */
	public record FieldDef(Component label, Kind kind, ForgeConfigSpec.ConfigValue<?> value,
	                       @Nullable List<Component> fixed) {

		public FieldDef(Component label, Kind kind, ForgeConfigSpec.ConfigValue<?> value) {
			this(label, kind, value, null);
		}

		public String getString() {
			return switch (kind) {
				case BOOL -> String.valueOf(((ForgeConfigSpec.BooleanValue) value).get());
				case INT -> String.valueOf(((ForgeConfigSpec.IntValue) value).get());
				case DOUBLE -> String.valueOf(((ForgeConfigSpec.DoubleValue) value).get());
				case STRING -> String.valueOf(((ForgeConfigSpec.ConfigValue<String>) value).get());
			};
		}

		public void set(String s) {
			switch (kind) {
				case BOOL -> ((ForgeConfigSpec.BooleanValue) value).set(Boolean.parseBoolean(s));
				case INT -> ((ForgeConfigSpec.IntValue) value).set(Integer.parseInt(s));
				case DOUBLE -> ((ForgeConfigSpec.DoubleValue) value).set(Double.parseDouble(s));
				case STRING -> ((ForgeConfigSpec.ConfigValue<String>) value).set(s);
			}
		}

		/**
		 * Resets this value to the default declared in the Forge config spec.
		 */
		public void reset() {
			switch (kind) {
				case BOOL -> ((ForgeConfigSpec.BooleanValue) value).set(((ForgeConfigSpec.BooleanValue) value).getDefault());
				case INT -> ((ForgeConfigSpec.IntValue) value).set(((ForgeConfigSpec.IntValue) value).getDefault());
				case DOUBLE -> ((ForgeConfigSpec.DoubleValue) value).set(((ForgeConfigSpec.DoubleValue) value).getDefault());
				case STRING ->
						((ForgeConfigSpec.ConfigValue<String>) value).set(((ForgeConfigSpec.ConfigValue<String>) value).getDefault());
			}
		}

		/**
		 * Tooltip lines for this value: the config name as the first line, then an explicit text
		 * when given, otherwise the translation {@code <modid>.configuration.<option>.tooltip}
		 * when one is registered, otherwise the comment that was declared with the Forge config value.
		 */
		@Nullable
		public List<Component> tooltip(ConfigEdit edit) {
			List<Component> ans = new ArrayList<>();
			ans.add(label.copy().withStyle(ChatFormatting.YELLOW));
			if (fixed != null) {
				ans.addAll(fixed);
				return ans;
			}
			List<String> path = value.getPath();
			String key = edit.configLangPrefix() + path.get(path.size() - 1) + ".tooltip";
			if (I18n.exists(key)) {
				ans.addAll(splitLines(I18n.get(key)));
				return ans;
			}
			Object vs = edit.commonSpec().getSpec().get(path);
			if (vs == null) vs = edit.clientSpec().getSpec().get(path);
			if (vs instanceof ForgeConfigSpec.ValueSpec vs2) {
				String comment = vs2.getComment();
				if (comment != null && !comment.isBlank()) {
					ans.addAll(splitLines(comment));
				}
			}
			return ans;
		}

		private static List<Component> splitLines(String text) {
			List<Component> ans = new ArrayList<>();
			for (String line : text.split("\n")) {
				String trimmed = line.trim();
				if (!trimmed.isEmpty()) ans.add(Component.literal(trimmed));
			}
			return ans;
		}

		public FormScreen.FormField toFormField(ConfigEdit edit) {
			Component[] tip = tooltip(edit) == null ? new Component[0] : tooltip(edit).toArray(new Component[0]);
			return switch (kind) {
				case BOOL -> FormScreen.FormField.bool(label, (Boolean) value.get(), tip);
				case INT -> FormScreen.FormField.text(label, String.valueOf(((ForgeConfigSpec.IntValue) value).get()),
						edit::validateInt, tip);
				case DOUBLE ->
						FormScreen.FormField.text(label, String.valueOf(((ForgeConfigSpec.DoubleValue) value).get()),
								edit::validateDouble, tip);
				case STRING -> FormScreen.FormField.text(label, String.valueOf(((ForgeConfigSpec.ConfigValue<String>) value).get()),
						null, tip);
			};
		}
	}

	/**
	 * A named group of config fields (a config section).
	 */
	public record Section(Component title, List<FieldDef> fields) {

	}

	protected abstract ForgeConfigSpec commonSpec();

	protected abstract ForgeConfigSpec clientSpec();

	/**
	 * Lang key prefix for config option/section names and tooltips, e.g.
	 * {@code l2hostility.configuration.}.
	 */
	protected abstract String configLangPrefix();

	/**
	 * Config id shown in the save toast, e.g. {@code l2hostility:config}.
	 */
	protected abstract ResourceLocation configId();

	/**
	 * All config sections edited from the config home.
	 */
	protected abstract List<Section> homeSections();

	/**
	 * Validation for integer config values. Override to provide mod-specific messages.
	 */
	@Nullable
	protected Component validateInt(String s) {
		try {
			Integer.parseInt(s.trim());
			return null;
		} catch (NumberFormatException e) {
			return EditorText.INVALID_NUMBER.get(s);
		}
	}

	/**
	 * Validation for double config values. Override to provide mod-specific messages.
	 */
	@Nullable
	protected Component validateDouble(String s) {
		try {
			Double.parseDouble(s.trim());
			return null;
		} catch (NumberFormatException e) {
			return EditorText.INVALID_NUMBER.get(s);
		}
	}

	/**
	 * Writes the common and client configs to disk. The values are already applied in memory via
	 * {@code set}.
	 */
	public void saveConfig() {
		saveIfMatch(ModConfig.Type.COMMON, commonSpec());
		saveIfMatch(ModConfig.Type.CLIENT, clientSpec());
	}

	private static void saveIfMatch(ModConfig.Type type, ForgeConfigSpec spec) {
		for (ModConfig c : ConfigTracker.INSTANCE.configSets().getOrDefault(type, Set.of())) {
			if (c.getSpec() == spec) {
				c.save();
			}
		}
	}

	/**
	 * Opens a form over the given fields; on confirm the values are applied and saved, and the
	 * parent screen is reopened (re-reading the config).
	 */
	public void openSectionForm(Component title, List<FieldDef> fields, Screen parent) {
		List<FormScreen.FormField> form = new ArrayList<>();
		for (FieldDef f : fields) form.add(f.toFormField(this));
		Minecraft.getInstance().setScreen(new FormScreen<>(title, new FormScreen.FormSpec<>(form, values -> {
			for (int i = 0; i < fields.size(); i++) fields.get(i).set(values.get(i));
			saveConfig();
			EditorToast.show(EditorText.SAVE.get(), EditorText.SAVE_DONE.get(configId()));
			return null;
		}), t -> Minecraft.getInstance().setScreen(parent), parent, true));
	}

	/**
	 * Resets every config value edited from the config home to its default and saves the config.
	 */
	public void resetToDefault() {
		for (Section s : homeSections()) {
			for (FieldDef f : s.fields()) f.reset();
		}
		saveConfig();
	}

}
