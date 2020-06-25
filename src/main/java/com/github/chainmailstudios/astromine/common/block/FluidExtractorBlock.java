package com.github.chainmailstudios.astromine.common.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

import com.github.chainmailstudios.astromine.common.block.base.DefaultedHorizontalFacingBlockWithEntity;
import com.github.chainmailstudios.astromine.common.block.entity.FluidExtractorBlockEntity;
import com.github.chainmailstudios.astromine.common.network.NetworkMember;
import com.github.chainmailstudios.astromine.common.network.NetworkType;
import com.github.chainmailstudios.astromine.registry.AstromineNetworkTypes;

public class FluidExtractorBlock extends DefaultedHorizontalFacingBlockWithEntity implements NetworkMember {
	public FluidExtractorBlock(Settings settings) {
		super(settings);
	}

	@Override
	public <T extends NetworkType> boolean acceptsType(T type) {
		return type == AstromineNetworkTypes.FLUID;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new FluidExtractorBlockEntity();
	}
}
