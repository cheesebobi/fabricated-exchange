package com.skirlez.fabricatedexchange.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.recipe.*;

@Mixin(SmithingTransformRecipe.class)
public interface LegacySmithingRecipeAccessor {
	@Accessor
	Ingredient getTemplate();
	@Accessor
	Ingredient getAddition();
	@Accessor
	Ingredient getBase();
}
