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

package com.github.chainmailstudios.astromine.technologies.common.block;

import com.github.chainmailstudios.astromine.common.block.base.WrenchableHorizontalFacingTieredBlockWithEntity;
import com.github.chainmailstudios.astromine.common.block.redstone.ComparatorMode;
import com.github.chainmailstudios.astromine.common.network.NetworkBlock;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.SolidifierBlockEntity;
import com.github.chainmailstudios.astromine.technologies.common.screenhandler.SolidifierScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SolidifierBlock extends WrenchableHorizontalFacingTieredBlockWithEntity implements NetworkBlock.FluidRequester {
	public SolidifierBlock(Properties settings) {
		super(settings);
	}

	public abstract static class Base extends SolidifierBlock {
		public Base(Properties settings) {
			super(settings);
		}

		@Override
		public boolean hasScreenHandler() {
			return true;
		}

		@Override
		public AbstractContainerMenu createScreenHandler(BlockState state, Level world, BlockPos pos, int syncId, Inventory playerInventory, Player player) {
			return new SolidifierScreenHandler(syncId, playerInventory.player, pos);
		}

		@Override
		public void populateScreenHandlerBuffer(BlockState state, Level world, BlockPos pos, ServerPlayer player, FriendlyByteBuf buffer) {
			buffer.writeBlockPos(pos);
		}

		@Override
		protected ComparatorMode getComparatorMode() {
			return ComparatorMode.FLUIDS;
		}
	}

	public static class Primitive extends SolidifierBlock.Base {
		public Primitive(Properties settings) {
			super(settings);
		}

		@Override
		public BlockEntity createBlockEntity() {
			return new SolidifierBlockEntity.Primitive();
		}
	}

	public static class Basic extends SolidifierBlock.Base {
		public Basic(Properties settings) {
			super(settings);
		}

		@Override
		public BlockEntity createBlockEntity() {
			return new SolidifierBlockEntity.Basic();
		}
	}

	public static class Advanced extends SolidifierBlock.Base {
		public Advanced(Properties settings) {
			super(settings);
		}

		@Override
		public BlockEntity createBlockEntity() {
			return new SolidifierBlockEntity.Advanced();
		}
	}

	public static class Elite extends SolidifierBlock.Base {
		public Elite(Properties settings) {
			super(settings);
		}

		@Override
		public BlockEntity createBlockEntity() {
			return new SolidifierBlockEntity.Elite();
		}
	}
}
