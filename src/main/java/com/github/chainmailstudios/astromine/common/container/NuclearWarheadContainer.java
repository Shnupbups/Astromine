package com.github.chainmailstudios.astromine.common.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;

import com.github.chainmailstudios.astromine.common.container.base.DefaultedBlockStateContainer;
import com.github.chainmailstudios.astromine.registry.AstromineContainers;

public class NuclearWarheadContainer extends DefaultedBlockStateContainer {
	public NuclearWarheadContainer(int synchronizationID, PlayerInventory playerInventory, BlockPos position) {
		super(synchronizationID, playerInventory, position);
	}

	@Override
	public ScreenHandlerType<?> getType() {
		return AstromineContainers.NUCLEAR_WARHEAD;
	}
}
