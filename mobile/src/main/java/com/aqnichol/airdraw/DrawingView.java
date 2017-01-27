package com.aqnichol.airdraw;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.aqnichol.movements.Absolute;

/**
 * TODO: document your custom view class.
 */
public class DrawingView extends View {
    private Absolute[] path;

    public DrawingView(Context context) {
        super(context);
        init(null, 0);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void setPath(Absolute[] path) {
        this.path = path;
    }

    private void init(AttributeSet attrs, int defStyle) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float density = Resources.getSystem().getDisplayMetrics().density;

        Paint p = new Paint();
        p.setColor(Color.BLACK);
        if (path == null || path.length == 0) {
            p.setTextSize(16 * density);
            canvas.drawText("No path", 10, 10, p);
            return;
        }

        p.setStrokeWidth(density * 5);
        p.setStyle(Paint.Style.STROKE);

        Translator tr = new Translator();
        Path pt = new Path();
        pt.moveTo(tr.convertX(path[0]), tr.convertY(path[0]));
        for (int i = 1; i < path.length; ++i) {
            Absolute a = path[i];
            pt.lineTo(tr.convertX(a), tr.convertY(a));
        }

        canvas.drawPath(pt, p);
    }

    private class Translator {
        private Absolute.BoundingBox bounds;
        private float scale;

        public Translator() {
            bounds = Absolute.bounds(path);
            float xScale = (float)getWidth() / bounds.height;
            float yScale = (float)getHeight() / bounds.depth;
            if (xScale < yScale) {
                scale = xScale;
            } else {
                scale = yScale;
            }
        }

        public float convertX(Absolute a) {
            return (a.y - bounds.minY) * scale;
        }

        public float convertY(Absolute a) {
            return (bounds.depth - (a.z - bounds.minZ)) * scale;
        }
    }
}
