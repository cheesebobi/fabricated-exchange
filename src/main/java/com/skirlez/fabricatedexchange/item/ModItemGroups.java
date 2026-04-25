package com.skirlez.fabricatedexchange.item;

import com.skirlez.fabricatedexchange.FabricatedExchange;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItemGroups {
	private ModItemGroups() {}

	public static final RegistryKey<ItemGroup> FABRICATED_EXCHANGE_KEY =
			RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(FabricatedExchange.MOD_ID, "fabricated-exchange"));

	public static final ItemGroup FABRICATED_EXCHANGE = FabricItemGroup.builder()
			.displayName(Text.translatable("itemgroup.fabricated-exchange"))
			.icon(() -> new ItemStack(ModItems.PHILOSOPHERS_STONE)).build();

	public static void register() {
		Registry.register(Registries.ITEM_GROUP, FABRICATED_EXCHANGE_KEY, FABRICATED_EXCHANGE);
	}
}
