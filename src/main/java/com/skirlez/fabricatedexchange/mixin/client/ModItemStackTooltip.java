package com.skirlez.fabricatedexchange.mixin.client;

import com.skirlez.fabricatedexchange.FabricatedExchangeClient;
import com.skirlez.fabricatedexchange.emc.EmcData;
import com.skirlez.fabricatedexchange.screen.TransmutationTableScreen;
import com.skirlez.fabricatedexchange.screen.slot.transmutation.TransmutationSlot;
import com.skirlez.fabricatedexchange.util.SuperNumber;
import com.skirlez.fabricatedexchange.util.config.ModDataFiles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ModItemStackTooltip {
	@Inject(method = "getTooltip", at = @At("RETURN"))
	private void addEmcToTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
		ItemStack itemStack = (ItemStack)(Object)this;
		List<Text> list = cir.getReturnValue();
		SuperNumber itemEmc = EmcData.getItemEmc(itemStack.getItem());

		EmcData.considerStackDurability(itemStack, itemEmc);
		EmcData.considerStackNbt(itemStack, itemEmc);

		int maxCount = itemStack.getMaxCount();
		MinecraftClient client = MinecraftClient.getInstance();

		if (ModDataFiles.MAIN_CONFIG_FILE.showEnchantedBookRepairCost && itemStack.getItem().equals(Items.ENCHANTED_BOOK)) {
			NbtCompound nbt = itemStack.getNbt();
			if (nbt != null) {
				int repairCost = nbt.getInt("RepairCost");
				if (repairCost != 0) {
					list.add(Text.literal("\u00a76Added Repair Cost: " + repairCost + "\u00a7r"));
				}
			}
		}

		if (!itemEmc.equalsZero()
				&& client.currentScreen instanceof TransmutationTableScreen currentScreen
				&& currentScreen.getFocusedSlot() instanceof TransmutationSlot
				&& Screen.hasShiftDown()
				&& maxCount != 1) {
			SuperNumber affordableCount = new SuperNumber(FabricatedExchangeClient.clientEmc);
			affordableCount.divide(itemEmc);
			affordableCount.floor();

			SuperNumber displayCount = SuperNumber.min(new SuperNumber(maxCount), affordableCount);
			if (displayCount.equalsOne()) {
				list.add(Text.literal("\u00a7eEMC\u00a7r: " + itemEmc));
			}
			else {
				SuperNumber totalAffordableEmc = new SuperNumber(itemEmc);
				totalAffordableEmc.multiply(displayCount);
				list.add(Text.literal("\u00a7eEMC for " + displayCount + ": \u00a7r" + totalAffordableEmc));
			}
		}
		else {
			if (!itemEmc.equalsZero()) {
				list.add(Text.literal("\u00a7eEMC\u00a7r: " + itemEmc));
				if (itemStack.getCount() > 1) {
					SuperNumber stackEmc = new SuperNumber(itemEmc);
					stackEmc.multiply(itemStack.getCount());
					list.add(Text.literal("\u00a7eStack EMC: \u00a7r" + stackEmc));
				}
			}

			NbtCompound nbt = itemStack.getNbt();
			if (nbt != null) {
				String storedEmcString = nbt.getString("emc");
				if (!storedEmcString.isEmpty()) {
					SuperNumber storedEmc = new SuperNumber(storedEmcString);
					list.add(Text.literal("\u00a7eStored EMC\u00a7r: " + storedEmc));
				}
			}
		}

		if (ModDataFiles.MAIN_CONFIG_FILE.showItemEmcOrigin) {
			boolean seed = EmcData.isItemInSeedValues(itemStack.getItem());
			boolean custom = EmcData.isItemInCustomValues(itemStack.getItem());
			if (seed) {
				list.add(Text.literal("\u00a7eSeed EMC"));
			}
			if (custom) {
				list.add(Text.literal("\u00a7eCustom EMC"));
			}
			if (!seed && !custom && !itemEmc.equalsZero()) {
				list.add(Text.literal("\u00a7eInferred EMC"));
			}
		}
	}
}
