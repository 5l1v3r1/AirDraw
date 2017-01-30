package com.aqnichol.airdraw;

import android.util.Log;

import java.util.Arrays;

/**
 * A 3x3 matrix for dealing with rotations.
 */
public class Matrix {
    public static class Vector {
        public float x;
        public float y;
        public float z;

        Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float norm() {
            return (float)Math.sqrt(x*x + y*y + z*z);
        }

        public void scale(float s) {
            x *= s;
            y *= s;
            z *= s;
        }
    }

    private float data[];

    /**
     * Create a matrix from the rotation vector sensor.
     *
     * @param x x output of the sensor
     * @param y y output of the sensor
     * @param z z output of the sensor
     */
    public Matrix(float x, float y, float z) {
        Vector u = new Vector(x, y, z);
        double angle = Math.asin(u.norm())*2;
        u.scale(1/u.norm());
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);
        // Formula from
        // https://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle
        data = new float[]{
                cos + u.x*u.x*(1-cos), u.x*u.y*(1-cos)-u.z*sin, u.x*u.z*(1-cos)+u.y*sin,
                u.y*u.x*(1-cos)+u.z*sin, cos+u.y*u.y*(1-cos), u.y*u.z*(1-cos)-u.x*sin,
                u.z*u.x*(1-cos)-u.y*sin, u.z*u.y*(1-cos)+u.x*sin, cos+u.z*u.z*(1-cos),
        };
    }

    public Vector apply(Vector v) {
        return new Vector(
                data[0]*v.x + data[1]*v.y + data[2]*v.z,
                data[3]*v.x + data[4]*v.y + data[5]*v.z,
                data[6]*v.x + data[7]*v.y + data[8]*v.z
        );
    }

    public void transpose() {
        data = new float[]{
                data[0], data[3], data[6],
                data[1], data[4], data[7],
                data[2], data[5], data[8],
        };
    }
}
