package com.skirlez.fabricatedexchange.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.skirlez.fabricatedexchange.FabricatedExchange;
import com.skirlez.fabricatedexchange.emc.EmcData;
import com.skirlez.fabricatedexchange.util.SuperNumber;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.text.NumberFormat;

public class EnergyCondenserScreen extends HandledScreen<EnergyCondenserScreenHandler> {
	private final int level;

	private final Identifier texture;
	private long emc;
	
	public EnergyCondenserScreen(EnergyCondenserScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.level = handler.getLevel();
		PacketByteBuf buf = handler.getAndConsumeCreationBuffer().get();
		this.emc = Long.parseLong(buf.readString());
		this.texture = new Identifier(FabricatedExchange.MOD_ID, "textures/gui/energy_condenser_mk" + (level + 1) + ".png");
	}

	@Override
	protected void init() {
		this.backgroundWidth = 256;
		this.backgroundHeight = 234;
		super.init();
		titleX = 0; 
		titleY = 0; 
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);
		int i = (width - backgroundWidth) / 2;
		int j = (height - backgroundHeight) / 2;

		this.playerInventoryTitleY = this.backgroundHeight - 93;

		context.drawTexture(texture, i, j, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
		
		
		ItemStack stack = handler.getTargetItemStack();
		if (stack.isEmpty())
			return;
		SuperNumber targetEmc = EmcData.getItemStackEmc(stack);
		if (targetEmc.equalsZero())
			return;
		double percent = ((double)emc / (double)targetEmc.toDouble());
		if (percent > 1d)
			percent = 1d;
		context.drawTexture(texture, x + 33, y + 9, 0, backgroundHeight, (int)(102 * percent), 10, 256, 256);
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		ItemStack stack = handler.getTargetItemStack();
		long targetEmc;
		if (stack.isEmpty())
			targetEmc = emc;
		else
			targetEmc = EmcData.getItemStackEmc(stack).toLong(-1);

		context.drawText(textRenderer, NumberFormat.getIntegerInstance().format(Long.min(emc, targetEmc)), 150, 10, 0x404040, false);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		renderBackground(context);
		super.render(context, mouseX, mouseY, delta);
		drawMouseoverTooltip(context, mouseX, mouseY);
	}

	public void update(long emc) {
		this.emc = emc;
	}
}
