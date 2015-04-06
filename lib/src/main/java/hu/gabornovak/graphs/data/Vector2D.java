package hu.gabornovak.graphs.data;

/**
 * @author Gabor Novak
 */
class Vector2D {
    private float x;
    private float y;

    public Vector2D() {
        x = y = 0.0f;
    }

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2D add(Vector2D v1) {
        Vector2D v2 = new Vector2D(x + v1.x, y + v1.y);
        return v2;
    }

    public Vector2D scale(float scaleFactor) {
        Vector2D v2 = new Vector2D(x * scaleFactor, y * scaleFactor);
        return v2;
    }

    public Vector2D normalize() {
        Vector2D v2 = new Vector2D();
        float length = (float) Math.sqrt(x * x + y * y);
        if (length != 0) {
            v2.x = x / length;
            v2.y = y / length;
        }
        return v2;
    }
}