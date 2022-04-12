/*
 *  Copyright (C) 2020 FALLET Nathan
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 */

package me.nathanfallet.replica.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import me.nathanfallet.replica.Replica;

public class ReplicaGenerator extends ChunkGenerator {

	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return Arrays.asList();
	}

	@Override
	public boolean canSpawn(World world, int x, int z) {
		return true;
	}

	public int xyzToByte(int x, int y, int z) {
		return (x * 16 + z) * 128 + y;
	}

	@Override
	public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
		ChunkData chunk = createChunkData(world);
		if (chunkX >= 0 && chunkX % Replica.DISTANCE == 0 && chunkZ % 2 == 0 && chunkZ >= 0 && chunkZ < 40) {
			for (int x = 2; x < 16; x++) {
				for (int z = 3; z < 15; z++) {
					chunk.setBlock(x, 63, z, Material.OAK_PLANKS);
					chunk.setBlock(x, 64, z, Material.OAK_PLANKS);
					if (z == 3 || z == 14 || x == 2) {
						chunk.setBlock(x, 65, z, Material.OAK_FENCE);
					}
				}
			}
			for (int y = 0; y < 11; y++) {
				for (int z = 4; z < 14; z++) {
					chunk.setBlock(14, 64 + y, z, Material.OAK_PLANKS);
					chunk.setBlock(15, 64 + y, z, Material.OAK_PLANKS);
				}
			}
		}
		return chunk;
	}

}
