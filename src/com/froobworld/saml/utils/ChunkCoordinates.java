package com.froobworld.saml.utils;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChunkCoordinates {
    private UUID worldUUID;
    private int x,z;

    public ChunkCoordinates(UUID worldUUID, int x, int z) {
        this.worldUUID = worldUUID;
        this.x = x;
        this.z = z;
    }


    public World getWorld() {
        return Bukkit.getWorld(worldUUID);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public Chunk toChunk() {
        return getWorld().getChunkAt(x, z);
    }

    @Override
    public String toString() {
        Map<String, Object> map = new HashMap<String, Object>();

        return worldUUID.toString() + ";" + x + ";" + z;
    }

    public static ChunkCoordinates fromString(String string) {
        String[] split = string.split(";");

        return new ChunkCoordinates(UUID.fromString(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]));
    }

    @Override
    public int hashCode() {
        return 31 * (31 + x) + z;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChunkCoordinates)) {
            return false;
        }
        ChunkCoordinates other = (ChunkCoordinates) obj;

        return other.worldUUID.equals(worldUUID) && other.x == x && other.z == z;
    }
}
