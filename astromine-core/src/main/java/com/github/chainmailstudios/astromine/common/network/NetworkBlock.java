package com.github.chainmailstudios.astromine.common.network;

import com.github.chainmailstudios.astromine.common.network.type.base.NetworkType;
import com.github.chainmailstudios.astromine.registry.AstromineNetworkTypes;
import net.minecraft.block.Block;

/**
 * An interface representing a {@link Block} which
 * provides a {@link NetworkMemberType} for one or more
 * {@link NetworkType}(s).
 */
public interface NetworkBlock {
	/**
	 * An interface representing a {@link NetworkBlock} whose
	 * {@link #getEnergyNetworkMemberType()}} defaults to
	 * {@link NetworkMemberType#REQUESTER}.
	 */
	interface EnergyRequester extends NetworkBlock {
		@Override
		default NetworkMemberType getEnergyNetworkMemberType() {
			return NetworkMemberType.REQUESTER;
		}
	}

	/**
	 * An interface representing a {@link NetworkBlock} whose
	 * {@link #getEnergyNetworkMemberType()}} defaults to
	 * {@link NetworkMemberType#PROVIDER}.
	 */
	interface EnergyProvider extends NetworkBlock {
		@Override
		default NetworkMemberType getEnergyNetworkMemberType() {
			return NetworkMemberType.PROVIDER;
		}
	}

	/**
	 * An interface representing a {@link NetworkBlock} whose
	 * {@link #getEnergyNetworkMemberType()}} defaults to
	 * {@link NetworkMemberType#BUFFER}.
	 */
	interface EnergyBuffer extends NetworkBlock {
		@Override
		default NetworkMemberType getEnergyNetworkMemberType() {
			return NetworkMemberType.BUFFER;
		}
	}

	/**
	 * An interface representing a {@link NetworkBlock} whose
	 * {@link #getEnergyNetworkMemberType()}} defaults to
	 * {@link NetworkMemberType#NODE}.
	 */
	interface EnergyNode extends NetworkBlock {
		@Override
		default NetworkMemberType getEnergyNetworkMemberType() {
			return NetworkMemberType.NODE;
		}
	}

	/**
	 * An interface representing a {@link NetworkBlock} whose
	 * {@link #getFluidNetworkMemberType()}} defaults to
	 * {@link NetworkMemberType#REQUESTER}.
	 */
	interface FluidRequester extends NetworkBlock {
		@Override
		default NetworkMemberType getFluidNetworkMemberType() {
			return NetworkMemberType.REQUESTER;
		}
	}

	/**
	 * An interface representing a {@link NetworkBlock} whose
	 * {@link #getFluidNetworkMemberType()}} defaults to
	 * {@link NetworkMemberType#PROVIDER}.
	 */
	interface FluidProvider extends NetworkBlock {
		@Override
		default NetworkMemberType getFluidNetworkMemberType() {
			return NetworkMemberType.PROVIDER;
		}
	}

	/**
	 * An interface representing a {@link NetworkBlock} whose
	 * {@link #getFluidNetworkMemberType()}} defaults to
	 * {@link NetworkMemberType#BUFFER}.
	 */
	interface FluidBuffer extends NetworkBlock {
		@Override
		default NetworkMemberType getFluidNetworkMemberType() {
			return NetworkMemberType.BUFFER;
		}
	}

	/**
	 * An interface representing a {@link NetworkBlock} whose
	 * {@link #getFluidNetworkMemberType()}} defaults to
	 * {@link NetworkMemberType#NODE}.
	 */
	interface FluidNode extends NetworkBlock {
		@Override
		default NetworkMemberType getFluidNetworkMemberType() {
			return NetworkMemberType.NODE;
		}
	}

	/** Returns the {@link NetworkMemberType} of the given
	 * {@link NetworkType}, or {@link NetworkMemberType#NODE}
	 * if the type is unknown. */
	default NetworkMemberType getMemberType(NetworkType type) {
		if (AstromineNetworkTypes.ENERGY.equals(type)) {
			return getEnergyNetworkMemberType();
		} else if (AstromineNetworkTypes.FLUID.equals(type)) {
			return getFluidNetworkMemberType();
		} else if (AstromineNetworkTypes.ITEM.equals(type)) {
			return getItemNetworkMemberType();
		}

		return NetworkMemberType.NODE;
	}

	/** Returns the default {@link NetworkMemberType} for {@link AstromineNetworkTypes#FLUID}. */
	default NetworkMemberType getFluidNetworkMemberType() {
		return NetworkMemberType.NONE;
	}

	/** Returns the default {@link NetworkMemberType} for {@link AstromineNetworkTypes#ENERGY}. */
	default NetworkMemberType getEnergyNetworkMemberType() {
		return NetworkMemberType.NONE;
	}

	/** Returns the default {@link NetworkMemberType} for {@link AstromineNetworkTypes#ITEM}. */
	default NetworkMemberType getItemNetworkMemberType() {
		return NetworkMemberType.NONE;
	}

	/** Asserts whether this block is a a {@link NetworkMember}
	 * of a network with the given {@link NetworkType} or not. */
	default boolean isMember(NetworkType type) {
		return getMemberType(type) != NetworkMemberType.NONE;
	}
}
