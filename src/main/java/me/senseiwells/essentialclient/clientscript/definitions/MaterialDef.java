package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.BuiltInFunction;
import me.senseiwells.arucas.utils.MemberFunction;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptMaterial;
import net.minecraft.block.Block;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import shadow.kotlin.Triple;

import java.util.*;

import static me.senseiwells.arucas.utils.Util.Types.STRING;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

@ClassDoc(
	name = MATERIAL,
	desc = {
		"This class represents all possible item and block types",
		"and allows you to convert them into instances of ItemStacks and Blocks"
	},
	importPath = "Minecraft"
)
public class MaterialDef extends CreatableDefinition<ScriptMaterial> {
	public MaterialDef(Interpreter interpreter) {
		super(MATERIAL, interpreter);
	}

	@Override
	public List<Triple<String, Object, Boolean>> defineStaticFields() {
		SortedMap<String, Object> map = new TreeMap<>();

		for (Item item : Registry.ITEM) {
			map.put(item.toString().toUpperCase(), item);
		}
		for (Block block : Registry.BLOCK) {
			map.put(block.toString().toUpperCase(), block);
		}

		List<Triple<String, Object, Boolean>> fields = new ArrayList<>(map.size());
		map.forEach((key, value) -> {
			fields.add(new Triple<>(key, value, false));
		});
		fields.add(new Triple<>("ALL", map.values(), false));
		return fields;
	}

	@Override
	public List<BuiltInFunction> defineStaticMethods() {
		return List.of(
			BuiltInFunction.of("of", 1, this::of)
		);
	}

	@FunctionDoc(
		isStatic = true,
		name = "of",
		desc = {
			"This converts a block or item id into a Material.",
			"This method will throw an error if the id is invalid"
		},
		params = {STRING, "id", "the id of the block or item"},
		returns = {MATERIAL, "the material instance from the id"},
		examples = "Material.of('diamond');"
	)
	private Object of(Arguments arguments) {
		String id = arguments.nextPrimitive(StringDef.class);
		Identifier identifier = ClientScriptUtils.stringToIdentifier(id);
		Optional<Item> item = Registry.ITEM.getOrEmpty(identifier);
		if (item.isEmpty()) {
			Optional<Block> block = Registry.BLOCK.getOrEmpty(identifier);
			if (block.isPresent()) {
				return block.get();
			}
		}
		return item.orElseThrow(() -> new RuntimeError("'%s' is not a valid Material".formatted(id)));
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getFullId", this::getFullId),
			MemberFunction.of("getId", this::getId),
			MemberFunction.of("asItemStack", this::asItemStack),
			MemberFunction.of("asBlock", this::asBlock),
			MemberFunction.of("getTranslatedName", this::getTranslatedName)
		);
	}

	@FunctionDoc(
		name = "getFullId",
		desc = "This returns the full id of the material, for example: 'minecraft:diamond'",
		returns = {STRING, "the full id representation of the material"},
		examples = "material.getFullId();"
	)
	private Object getFullId(Arguments arguments) {
		ScriptMaterial material = arguments.nextPrimitive(this);
		return material.getId().toString();
	}

	@FunctionDoc(
		name = "getId",
		desc = "This returns the id of the material, for example: 'diamond'",
		returns = {STRING, "the id representation of the material"},
		examples = "material.getId();"
	)
	private Object getId(Arguments arguments) {
		ScriptMaterial material = arguments.nextPrimitive(this);
		return material.getId().getPath();
	}

	@FunctionDoc(
		name = "asItemStack",
		desc = {
			"This converts the material into an ItemStack.",
			"If it cannot be converted an error will be thrown"
		},
		returns = {ITEM_STACK, "the ItemStack representation of the material"},
		examples = "material.asItemStack();"
	)
	private Object asItemStack(Arguments arguments) {
		ScriptMaterial material = arguments.nextPrimitive(this);
		return material.asItemStack();
	}

	@FunctionDoc(
		name = "asBlock",
		desc = {
			"This converts the material into a Block.",
			"If it cannot be converted an error will be thrown"
		},
		returns = {BLOCK, "the Block representation of the material"},
		examples = "material.asBlock();"
	)
	private Object asBlock(Arguments arguments) {
		ScriptMaterial material = arguments.nextPrimitive(this);
		return material.asBlockState();
	}

	@FunctionDoc(
		name = "getTranslatedName",
		desc = {
			"This gets the translated name of the ItemStack, for example: ",
			"Material.DIAMOND_SWORD would return 'Diamond Sword' if your language is English"
		},
		returns = {STRING, "the translated name of the Material"},
		examples = "material.getTranslatedName();"
	)
	private Object getTranslatedName(Arguments arguments) {
		ScriptMaterial material = arguments.nextPrimitive(this);
		return I18n.translate(material.getTranslationKey());
	}
}
