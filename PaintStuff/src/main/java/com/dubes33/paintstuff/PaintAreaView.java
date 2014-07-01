package com.dubes33.paintstuff;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jace on 9/20/13.
 */
public class PaintAreaView extends View
{
    ArrayList<Float> vertices = new ArrayList<Float>();
    Paint linePaint;
    int activeColor;
    boolean playingForward = false;
    boolean playingReverse = false;

    public PaintAreaView(Context context, ArrayList<Float> vertices)
    {
        super(context);

        setBackgroundColor(0xFFFFFFFF);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setActiveColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(playingForward == true && playingReverse == false)
        {
            for(int i = 0; i < vertices.size(); i +=3)
            {
                setActiveColor(vertices.get(i + 2));
                canvas.drawCircle(vertices.get(i), vertices.get(i + 1), 20, linePaint);
            }
        }
        else if(playingReverse == true && playingForward == false)
        {
            for(int i = vertices.size() - 1; i > 0; i -=3)
            {
                setActiveColor(vertices.get(i));
                canvas.drawCircle(vertices.get(i-2), vertices.get(i-1), 20, linePaint);
            }
        }
        else
        {
            for(int i = 0; i < vertices.size(); i +=3)
            {
                setActiveColor(vertices.get(i+2));
                canvas.drawCircle(vertices.get(i), vertices.get(i+1), 20, linePaint);
            }
        }
        playingForward = playingReverse = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(widthMeasureSpec, (int)(MeasureSpec.getSize(heightMeasureSpec) * 0.80));
    }

    public void setActiveColor(float activeColor)
    {
        linePaint.setColor((int)activeColor);
        this.activeColor = (int)activeColor;
    }

    @Override
    protected Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();

        bundle.putParcelable("super", super.onSaveInstanceState());
        bundle.putSerializable("vertices", vertices);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if(state instanceof Bundle)
        {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable("super"));
            vertices = (ArrayList)bundle.getSerializable("vertices");
        }
    }

    public void playForward()
    {
        playingForward = true;
        playingReverse = false;
        invalidate();
    }

    public void playReverse()
    {
        playingReverse = true;
        playingForward = false;
        invalidate();
    }

    public void showDrawing(ArrayList<Float> vertices, double percentBegin, double percentEnd)
    {
        if(percentEnd > 1.0)
            percentEnd = 1.0;
        else if(percentEnd < 0)
            percentEnd = 0;

        this.vertices.clear();
        double sampleSize = ((vertices.size() / 3)  * percentEnd * 3) - (((vertices.size() / 3)  * percentEnd * 3) % 3);
        for(double i = percentBegin; i < sampleSize; i++)
        {
            this.vertices.add(vertices.get((int) i));
        }
        invalidate();
    }
}
