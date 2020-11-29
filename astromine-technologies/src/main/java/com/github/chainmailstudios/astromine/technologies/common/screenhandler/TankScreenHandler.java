/*
 * MIT License
 *
 * Copyright (c) 2020 Chainmail Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.chainmailstudios.astromine.technologies.common.screenhandler;

import com.github.chainmailstudios.astromine.common.component.general.base.FluidComponent;
import com.github.chainmailstudios.astromine.common.item.base.FluidVolumeItem;
import com.github.chainmailstudios.astromine.common.screenhandler.base.block.ComponentBlockEntityFluidItemScreenHandler;
import com.github.chainmailstudios.astromine.common.widget.blade.FluidFilterWidget;
import com.github.chainmailstudios.astromine.common.widget.blade.HorizontalArrowWidget;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.TankBlockEntity;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesScreenHandlers;
import com.github.vini2003.blade.common.miscellaneous.Position;
import com.github.vini2003.blade.common.miscellaneous.Size;
import com.github.vini2003.blade.common.widget.base.SlotWidget;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class TankScreenHandler extends ComponentBlockEntityFluidItemScreenHandler {
	private TankBlockEntity tank;

	public TankScreenHandler(int syncId, Player player, BlockPos position) {
		super(AstromineTechnologiesScreenHandlers.TANK, syncId, player, position);
		tank = (TankBlockEntity) blockEntity;
	}

	public TankScreenHandler(MenuType<?> type, int syncId, Player player, BlockPos position) {
		super(type, syncId, player, position);
		tank = (TankBlockEntity) blockEntity;
	}

	@Override
	public void initialize(int width, int height) {
		super.initialize(width, height);

		fluidBar.setPosition(Position.of(width / 2F - fluidBar.getWidth() / 2F, fluidBar.getY()));

		SlotWidget input = new SlotWidget(0, blockEntity);
		input.setPosition(Position.of(fluidBar, -18 - 3, 0));
		input.setSize(Size.of(18, 18));

		SlotWidget output = new SlotWidget(1, blockEntity);
		output.setPosition(Position.of(fluidBar, -18 - 3, fluidBar.getHeight() - 18));
		output.setSize(Size.of(18, 18));

		HorizontalArrowWidget leftArrow = new HorizontalArrowWidget();
		leftArrow.setPosition(Position.of(input, 28, 0));
		leftArrow.setSize(Size.of(22, 16));
		leftArrow.setLimitSupplier(() -> {
			ItemStack stack = blockEntity.getItemComponent().getFirst();

			if (stack.getItem() instanceof FluidVolumeItem) {
				return (((FluidVolumeItem) stack.getItem()).getSize().intValue());
			} else {
				return 0;
			}
 		});
		leftArrow.setProgressSupplier(() -> {
			ItemStack stack = blockEntity.getItemComponent().getFirst();

			if (stack.getItem() instanceof FluidVolumeItem) {
				return FluidComponent.get(stack.getItem()).getFirst().getAmount().intValue();
			} else {
				return 0;
			}
		});

		HorizontalArrowWidget rightArrow = new HorizontalArrowWidget();
		rightArrow.setPosition(Position.of(output, -34, 0));
		rightArrow.setSize(Size.of(22, 16));
		rightArrow.setLimitSupplier(() -> {
			ItemStack stack = blockEntity.getItemComponent().getSecond();

			if (stack.getItem() instanceof FluidVolumeItem) {
				return (((FluidVolumeItem) stack.getItem()).getSize().intValue());
			} else {
				return 0;
			}
		});
		rightArrow.setProgressSupplier(() -> {
			ItemStack stack = blockEntity.getItemComponent().getSecond();

			if (stack.getItem() instanceof FluidVolumeItem) {
				return FluidComponent.get(stack.getItem()).getFirst().getAmount().intValue();
			} else {
				return 0;
			}
		});

		FluidFilterWidget filter = new FluidFilterWidget();
		filter.setPosition(Position.of(input, 5F, 18F + 2F));
		filter.setSize(Size.of(8, 8));
		filter.setFluidConsumer(fluid -> {
			tank.setFilter(fluid);
		});
		filter.setFluidSupplier(() -> tank.getFilter());

		mainTab.addWidget(input);
		mainTab.addWidget(output);

		mainTab.addWidget(filter);
	}
}
