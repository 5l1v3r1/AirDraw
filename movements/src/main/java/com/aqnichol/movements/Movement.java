package com.aqnichol.movements;

import java.nio.ByteBuffer;

/**
 * A period of constant acceleration.
 *
 * It has three vector components in some unknown unit.
 * It has one time component in seconds.
 */
public class Movement {
    public static class UnmarshalException extends Exception {
        UnmarshalException(String message) {
            super(message);
        }
    }

    public float t = 0;
    public float x = 0;
    public float y = 0;
    public float z = 0;

    public Movement() {
    }

    public Movement(float t, float x, float y, float z) {
        this.t = t;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static byte[] marshal(Movement[] movements) {
        ByteBuffer res = ByteBuffer.allocate(16*movements.length);
        for (int i = 0; i < movements.length; ++i) {
            Movement m = movements[i];
            res.putFloat(m.t);
            res.putFloat(m.x);
            res.putFloat(m.y);
            res.putFloat(m.z);
        }
        return res.array();
    }

    public static Movement[] unmarshal(byte[] movements) throws UnmarshalException {
        if (movements.length % 16 != 0) {
            throw new UnmarshalException("invalid movement byte length: " + movements.length);
        }
        ByteBuffer buf = ByteBuffer.wrap(movements);
        Movement[] res = new Movement[movements.length / 16];
        for (int i = 0; i < res.length; ++i) {
            res[i] = new Movement();
            res[i].t = buf.getFloat();
            res[i].x = buf.getFloat();
            res[i].y = buf.getFloat();
            res[i].z = buf.getFloat();
        }
        return res;
    }
}

