package com.aqnichol.movements;

/**
 * An "absolute" position in space.
 */
public class Absolute {
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

    public Absolute(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Approximate the double integral of accelerations to produce
     * absolute positions.
     * @return The absolute positions.
     */
    public static Absolute[] integrate(Movement[] accel) {
        float velX = 0, velY = 0, velZ = 0;
        float x = 0, y = 0, z = 0;
        Absolute[] res = new Absolute[accel.length];
        for (int i = 0; i < accel.length; ++i) {
            Movement a = accel[i];
            x += velX*a.t;
            y += velY*a.t;
            z += velZ*a.t;
            velX += a.x*a.t;
            velY += a.y*a.t;
            velZ += a.z*a.t;
            res[i] = new Absolute(x, y, z);
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
