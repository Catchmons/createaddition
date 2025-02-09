package com.mrh0.createaddition.blocks.modular_accumulator;

import java.util.List;

import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.logistics.block.display.DisplayLinkContext;
import com.simibubi.create.content.logistics.block.display.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModularAccumulatorDisplaySource extends PercentOrProgressBarDisplaySource {

	@Override
	protected MutableComponent formatNumeric(DisplayLinkContext context, Float currentLevel) {
		int mode = getMode(context);
		if (mode == 1)
			return super.formatNumeric(context, currentLevel);
		return Util.getTextComponent(Math.round(currentLevel), "fe");
	}

	private int getMode(DisplayLinkContext context) {
		return context.sourceConfig()
			.getInt("Mode");
	}

	@Override
	protected Float getProgress(DisplayLinkContext context) {
		if (!(context.getSourceTE() instanceof ModularAccumulatorTileEntity te)) return null;
		te = te.getControllerTE();
		if(te == null) return null;
		float capacity = te.energyStorage.getMaxEnergyStored();
		float stored = te.energyStorage.getEnergyStored();

		if (capacity == 0) return 0f;

		return switch (getMode(context)) {
			case 0, 1 -> stored / capacity;
			case 2 -> stored;
			case 3 -> capacity;
			case 4 -> capacity - stored;
			default -> 0f;
		};
	}

	@Override
	protected boolean allowsLabeling(DisplayLinkContext context) {
		return true;
	}

	@Override
	protected boolean progressBarActive(DisplayLinkContext context) {
		return getMode(context) == 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder,
		boolean isFirstLine) {
		super.initConfigurationWidgets(context, builder, isFirstLine);
		if (isFirstLine)
			return;
		builder.addSelectionScrollInput(0, 120,
			(si, l) -> si
				.forOptions(List.of(
							new TranslatableComponent("createaddition.display_source.accumulator.progress_bar"),
							new TranslatableComponent("createaddition.display_source.accumulator.percent"),
							new TranslatableComponent("createaddition.display_source.accumulator.current"),
							new TranslatableComponent("createaddition.display_source.accumulator.max"),
							new TranslatableComponent("createaddition.display_source.accumulator.remaining")
						)) // Lang.translatedOptions("display_source.kinetic_stress", "progress_bar", "percent", "current", "max", "remaining")
				.titled(new TranslatableComponent("createaddition.display_source.accumulator.display")), "Mode");
	}

	@Override
	protected String getTranslationKey() {
		return "modular_accumulator";
	}
}