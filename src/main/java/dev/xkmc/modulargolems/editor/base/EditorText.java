package dev.xkmc.modulargolems.editor.base;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;

public enum EditorText {
	NEW("editor.new", "New", 0, null),
	EDIT("editor.edit", "Edit", 0, null),
	REMOVE("editor.remove", "Remove", 0, null),
	SAVE("editor.save", "Save", 0, null),
	BACK("editor.back", "Back", 0, null),
	ADD("editor.add", "Add", 0, null),
	CANCEL("editor.cancel", "Cancel", 0, null),
	CONFIRM("editor.confirm", "Confirm", 0, null),
	SEARCH("editor.search", "Search...", 0, null),
	VALUE("editor.value", "Value", 0, null),
	PICK_TARGET("editor.pick_target", "Pick target", 0, null),
	PICK_ITEM("editor.pick_item", "Pick item", 0, null),
	PICK_TAG("editor.pick_tag", "Pick tag", 0, null),
	CLEAR("editor.clear", "Clear", 0, null),
	NO_FILE("editor.no_file", "Select a file first", 0, ChatFormatting.RED),
	INVALID_NUMBER("editor.invalid_number", "Not a valid number: %s", 1, ChatFormatting.RED),
	LEVEL_FULL("editor.level_full", "Lv %s/%s", 2, null),
	LEVEL_RANGE("editor.level_range", "Level (1-%s)", 1, null),
	INVALID_INT("editor.invalid_int", "Not a valid integer 1..%s: %s", 2, ChatFormatting.RED),
	RELOAD("editor.reload", "Reload", 0, null),
	RELOAD_TITLE("editor.reload_title", "Reload datapacks?", 0, null),
	RELOAD_NOTE("editor.reload_note", "Run the datapack reload now, or do it manually later?", 0, ChatFormatting.GRAY),
	RELOAD_NOW("editor.reload_now", "Reload now", 0, null),
	RELOAD_DONE("editor.reload_done", "Reloading datapacks...", 0, null),
	LATER("editor.later", "Later", 0, null),
	DISCARD("editor.discard", "Discard", 0, null),
	UNSAVED_TITLE("editor.unsaved_title", "Unsaved changes", 0, null),
	UNSAVED_NOTE("editor.unsaved_note", "You have unsaved changes. Save them before leaving?", 0, ChatFormatting.GRAY);

	private final String key, def;
	private final int arg;
	private final ChatFormatting format;

	EditorText(String key, String def, int arg, @Nullable ChatFormatting format) {
		this.key = key;
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
		for (EditorText lang : EditorText.values()) {
			pvd.add(lang.key, lang.def);
		}
	}

}
