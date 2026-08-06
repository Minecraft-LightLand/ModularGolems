package dev.xkmc.modulargolems.editor;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;

public enum EditorLang {
	TITLE("editor.title", "Modular Golems Datapack Editor", 0, null),
	OPEN("editor.open", "Edit Datapacks", 0, null),
	MATERIALS("editor.materials", "Materials", 0, null),
	PARTS("editor.parts", "Parts", 0, null),
	MATERIALS_FILE("editor.materials_file", "Materials file", 0, null),
	PARTS_FILE("editor.parts_file", "Parts file", 0, null),
	FILE("editor.file", "File: %s", 1, null),
	NEW("editor.new", "New", 0, null),
	EDIT("editor.edit", "Edit", 0, null),
	REMOVE("editor.remove", "Remove", 0, null),
	SAVE("editor.save", "Save", 0, null),
	BACK("editor.back", "Back", 0, null),
	ADD("editor.add", "Add", 0, null),
	CANCEL("editor.cancel", "Cancel", 0, null),
	CONFIRM("editor.confirm", "Confirm", 0, null),
	SEARCH("editor.search", "Search...", 0, null),
	FILE_ID("editor.file_id", "File ID (namespace:path)", 0, null),
	NAMESPACE_HINT("editor.namespace_hint", "Namespace must be a loaded mod or start with '_'", 0, ChatFormatting.YELLOW),
	SAVE_DONE("editor.save_done", "Saved to %s", 1, null),
	SAVE_FAIL("editor.save_fail", "Failed to save: %s", 1, ChatFormatting.RED),
	SAVE_NOTE("editor.save_note", "Save to active world datapacks. Restart the world (or enable the pack in Datapack Selection) to apply.", 0, ChatFormatting.GRAY),
	NOT_IN_WORLD("editor.not_in_world", "Must be in a world to save", 0, ChatFormatting.RED),
	INGREDIENT("editor.ingredient", "Ingredient", 0, null),
	REPAIR("editor.repair", "Repair ingredient", 0, null),
	LIMITATION("editor.limitation", "Part limitation: %s", 1, null),
	PICK_ITEM("editor.pick_item", "Pick item", 0, null),
	PICK_TAG("editor.pick_tag", "Pick tag", 0, null),
	CLEAR("editor.clear", "Clear", 0, null),
	STATS("editor.stats", "Stats (%s)", 1, null),
	MODIFIERS("editor.modifiers", "Modifiers (%s)", 1, null),
	VALUE("editor.value", "Value", 0, null),
	LEVEL("editor.level", "Level", 0, null),
	ADD_STAT("editor.add_stat", "Add stat", 0, null),
	ADD_MODIFIER("editor.add_modifier", "Add modifier", 0, null),
	ADD_PART("editor.add_part", "Add part", 0, null),
	ADD_TYPE("editor.add_type", "Add golem type", 0, null),
	ADD_MAGNIFIER("editor.add_magnifier", "Add entity", 0, null),
	ADD_FILTER("editor.add_filter", "Add part", 0, null),
	SELECT_TYPE("editor.select_type", "Select golem type", 0, null),
	SELECT_STAT("editor.select_stat", "Select stat", 0, null),
	SELECT_MODIFIER("editor.select_modifier", "Select modifier", 0, null),
	SELECT_ITEM("editor.select_item", "Select item", 0, null),
	SELECT_TAG("editor.select_tag", "Select tag", 0, null),
	SELECT_PART("editor.select_part", "Select part", 0, null),
	SELECT_ENTITY("editor.select_entity", "Select entity type", 0, null),
	FILTERS("editor.filters", "Filters (%s)", 1, null),
	MAGNIFIERS("editor.magnifiers", "Magnifiers (%s)", 1, null),
	ENTRIES("editor.entries", "%s entries", 1, null),
	PICK_TARGET("editor.pick_target", "Pick target", 0, null),
	CONFIRM_DELETE("editor.confirm_delete", "Remove %s?", 1, null),
	CONFIRM_DELETE_NOTE("editor.confirm_delete_note", "Removed only from the working copy. Press Save to persist.", 0, ChatFormatting.GRAY),
	NO_FILE("editor.no_file", "Select a file first", 0, ChatFormatting.RED),
	RELOAD("editor.reload", "Reload", 0, null),
	DISCARD("editor.discard", "Discard", 0, null),
	UNSAVED_TITLE("editor.unsaved_title", "Unsaved changes", 0, null),
	UNSAVED_NOTE("editor.unsaved_note", "You have unsaved changes. Save them before leaving?", 0, ChatFormatting.GRAY),
	INVALID_ID("editor.invalid_id", "Invalid ResourceLocation: %s", 1, ChatFormatting.RED),
	INVALID_NUMBER("editor.invalid_number", "Not a valid number: %s", 1, ChatFormatting.RED),
	INVALID_INT("editor.invalid_int", "Not a valid integer 1..5: %s", 1, ChatFormatting.RED),
	SAVE_TO("editor.save_to", "Save to file", 0, null),
	GOLEM_STAT("editor.golem_stat", "Stat", 0, null),
	STAT_FILTER("editor.stat_filter", "Filter type", 0, null),
	NO_MATERIALS("editor.no_materials", "No materials loaded. Open a world first.", 0, ChatFormatting.GRAY),
	NO_PART_FILTERS("editor.no_part_filters", "No part filters", 0, ChatFormatting.GRAY),
	NO_MAGNIFIERS("editor.no_magnifiers", "No magnifiers", 0, ChatFormatting.GRAY),
	EMPTY_FILE("editor.empty_file", "Empty (no entries)", 0, ChatFormatting.GRAY),
	PART_TYPE("editor.part_type", "Part", 0, null),
	MATERIAL("editor.material", "Material", 0, null),
	NEW_FILE_TITLE("editor.new_file_title", "New %s file", 1, null),
	EDIT_FILE_TITLE("editor.edit_file_title", "Edit %s", 1, null);

	private final String key, def;
	private final int arg;
	private final ChatFormatting format;

	EditorLang(String key, String def, int arg, @Nullable ChatFormatting format) {
		this.key = ModularGolems.MODID + "." + key;
		this.def = def;
		this.arg = arg;
		this.format = format;
	}

	public String key() {
		return key;
	}

	public MutableComponent get(Object... args) {
		if (args.length != arg)
			throw new IllegalArgumentException("for " + name() + ": expect " + arg + " parameters, got " + args.length);
		MutableComponent ans = Component.translatable(key, args);
		if (format != null) {
			ans = ans.withStyle(format);
		}
		return ans;
	}

	public static void genLang(RegistrateLangProvider pvd) {
		for (EditorLang lang : EditorLang.values()) {
			pvd.add(lang.key, lang.def);
		}
	}

}
