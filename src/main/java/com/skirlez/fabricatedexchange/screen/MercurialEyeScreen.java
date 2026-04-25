package com.skirlez.fabricatedexchange.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.skirlez.fabricatedexchange.FabricatedExchange;
import com.skirlez.fabricatedexchange.item.EmcStoringItem;
import com.skirlez.fabricatedexchange.item.ModItems;
import com.skirlez.fabricatedexchange.util.SuperNumber;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MercurialEyeScreen extends HandledScreen<MercurialEyeScreenHandler> {




	private static final Identifier TEXTURE =
		new Identifier(FabricatedExchange.MOD_ID, "textures/gui/mercurial_eye.png");

	private int backgroundWidth = 176;
	private int backgroundHeight = 176;

	public MercurialEyeScreen(MercurialEyeScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		renderBackground(context);
		super.render(context, mouseX, mouseY, delta);
		drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - backgroundWidth) / 2;
		int y = (height - backgroundHeight) / 2;
		context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		super.drawForeground(context, mouseX, mouseY);

		MinecraftClient client = MinecraftClient.getInstance();
		ItemStack stack = client.player.getMainHandStack();
		if (stack.getItem() != ModItems.MERCURIAL_EYE) {
			stack = client.player.getOffHandStack();
			if (stack.getItem() != ModItems.MERCURIAL_EYE)
				return;
		}


		SuperNumber emc = EmcStoringItem.getStoredEmc(stack);
		String str = emc.toString();
		context.drawText(textRenderer, str, (int)(88 - textRenderer.getWidth(str) / 2f), 15, 0x404040, false);
	}
}
