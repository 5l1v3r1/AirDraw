package com.aqnichol.movements;

import java.nio.ByteBuffer;

/**
 * An "absolute" position in space.
 */
public class Absolute {
    public static class UnmarshalException extends Exception {
        UnmarshalException(String message) {
            super(message);
        }
    }

    public static class BoundingBox {
        public float minX = 0;
        public float minY = 0;
        public float minZ = 0;
        public float width = 0;
        public float height = 0;
        public float depth = 0;
    }

    public float x;
    public float y;
    public float z;

    public Absolute() {
        x = 0;
        y = 0;
        z = 0;
    }

    public Absolute(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static byte[] marshal(Absolute[] abs) {
        ByteBuffer res = ByteBuffer.allocate(12*abs.length);
        for (Absolute a : abs) {
            res.putFloat(a.x);
            res.putFloat(a.y);
            res.putFloat(a.z);
        }
        return res.array();
    }

    public static Absolute[] unmarshal(byte[] data) throws Absolute.UnmarshalException {
        if (data.length % 12 != 0) {
            throw new Absolute.UnmarshalException("invalid byte length: " + data.length);
        }
        ByteBuffer buf = ByteBuffer.wrap(data);
        Absolute[] res = new Absolute[data.length / 12];
        for (int i = 0; i < res.length; ++i) {
            res[i] = new Absolute();
            res[i].x = buf.getFloat();
            res[i].y = buf.getFloat();
            res[i].z = buf.getFloat();
        }
        return res;
    }

    /**
     * Get the bounding box of the points.
     * @return The bounding box.
     */
    public static BoundingBox bounds(Absolute[] pos) {
        if (pos.length == 0) {
            return new BoundingBox();
        }
        float minX = pos[0].x;
        float minY = pos[0].y;
        float minZ = pos[0].z;
        float maxX = minX;
        float maxY = minY;
        float maxZ = minY;
        for (Absolute p : pos) {
            if (p.x < minX) {
                minX = p.x;
            }
            if (p.x > maxX) {
                maxX = p.x;
            }
            if (p.y < minY) {
                minY = p.y;
            }
            if (p.y > maxY) {
                maxY = p.y;
            }
            if (p.z < minZ) {
                minZ = p.z;
            }
            if (p.z > maxZ) {
                maxZ = p.z;
            }
        }
        BoundingBox res = new BoundingBox();
        res.minX = minX;
        res.minY = minY;
        res.minZ = minZ;
        res.width = maxX - minX;
        res.height = maxY - minY;
        res.depth = maxZ - minZ;
        return res;
    }
}
