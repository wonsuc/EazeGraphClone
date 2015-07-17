package com.example.w.eazegraphclone.models;

/**
 * 좌표값을 나탄내기 위한 Simple wrapper class
 */
public class Point2D {

    private float mX;
    private float mY;

    public Point2D(float _x, float _y) {
        mX = _x;
        mY = _y;
    }

    public Point2D() {
    }

    public float getX() {
        return mX;
    }

    public void setX(float _x) {
        mX = _x;
    }

    public float getY() {
        return mY;
    }

    public void setY(float _y) {
        mY = _y;
    }

    public float[] getFloatArray() {
        return new float[]{mX, mY};
    }
}
