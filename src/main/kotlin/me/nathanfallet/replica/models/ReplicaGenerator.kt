/*
 *  Copyright (C) 2023 FALLET Nathan
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

package me.nathanfallet.replica.models

import java.util.Arrays
import java.util.Random

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.ChunkGenerator

import me.nathanfallet.replica.Replica

class ReplicaGenerator: ChunkGenerator() {

    override fun getDefaultPopulators(world: World): List<BlockPopulator> {
        return listOf()
    }

    override fun canSpawn(world: World, x: Int, z: Int): Boolean {
        return true
    }

    fun xyzToByte(x: Int, y: Int, z: Int): Int {
        return (x * 16 + z) * 128 + y
    }

    override fun generateChunkData(world: World, random: Random, chunkX: Int, chunkZ: Int, biome: BiomeGrid): ChunkData {
        val chunk = createChunkData(world)
        if (chunkX >= 0 && chunkX % Replica.distance == 0 && chunkZ % 2 == 0 && chunkZ >= 0 && chunkZ < 40) {
            for (x in 2..15) {
                for (z in 3..14) {
                    chunk.setBlock(x, 63, z, Material.OAK_PLANKS)
                    chunk.setBlock(x, 64, z, Material.OAK_PLANKS)
                    if (z == 3 || z == 14 || x == 2) {
                        chunk.setBlock(x, 65, z, Material.OAK_FENCE)
                    }
                }
            }
            for (y in 0..10) {
                for (z in 4..13) {
                    chunk.setBlock(14, 64 + y, z, Material.OAK_PLANKS)
                    chunk.setBlock(15, 64 + y, z, Material.OAK_PLANKS)
                }
            }
        }
        return chunk
    }

}
