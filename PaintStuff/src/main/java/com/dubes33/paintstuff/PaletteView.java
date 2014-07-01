package com.dubes33.paintstuff;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.FloatMath;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Jace on 9/20/13.
 */
public class PaletteView extends ViewGroup
{
    public interface OnColorChangedListener
    {
        public void getSelectedColor(int color);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener)
    {
        onColorChangedListener = listener;
    }

    public Context context;
    public ArrayList<PaintView> allPaintViews;
    public int selectedColor;
    public OnColorChangedListener onColorChangedListener = null;
    public boolean inMixMode = false;
    public Button mixButton;
    public Button removeButton;

    public PaletteView(Context context)
    {
        super(context);

        this.context = context;
        allPaintViews = new ArrayList<PaintView>();

            this.setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
//        this.setY(MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean bool, int i, int i2, int i3, int i4)
    {
        int bWidth = (int)(Math.min(getWidth(), getHeight()) * 0.10f);
        int bHeight = (int)(Math.min(getWidth(), getHeight()) * 0.10f);

        float max = Math.max(getWidth(), getHeight());
        float min = Math.min(getWidth(), getHeight());

        mixButton = (Button) getChildAt(0);
        removeButton = (Button) getChildAt(1);

        //Portrait mode
        if(getWidth() < getHeight())
        {
            mixButton.layout((int) (min * 0.5f) - bWidth, (int) (max * 0.43f) - bHeight,
                    (int) (min * 0.5f) + bWidth, (int) (max * 0.43f) + bHeight);

            removeButton.layout((int) (min * 0.5f) - bWidth, (int) (max * 0.57f) - bHeight,
                    (int) (min * 0.5f) + bWidth, (int) (max * 0.57f) + bHeight);
        }
        else
        {
            mixButton.layout((int) (max * 0.43f) - bWidth, (int) (min * 0.5f) - bHeight,
                    (int) (max * 0.43f) + bWidth, (int) (min * 0.5f) + bHeight);

            removeButton.layout((int) (max * 0.57f) - bWidth, (int) (min * 0.5f) - bHeight,
                    (int) (max * 0.57f) + bWidth, (int) (min * 0.5f) + bHeight);
        }

        for(int childIndex = 2; childIndex < getChildCount(); childIndex++)
        {
            View child = getChildAt(childIndex);

            child.setRotation(45);

            //Changes child width/height according to screen size
            float childWidth = Math.min(getWidth(), getHeight()) * 0.115f;
            float childHeight = Math.min(getWidth(), getHeight()) * 0.115f;


            //Variables used to compute child position on screen
            float angle = (float)(2.0 * Math.PI) * ((float)childIndex / ((float)getChildCount() - 3));
            float a = getWidth() * 0.5f - (getWidth() * 0.08f);
            float b = getHeight() * 0.5f - (getHeight() * 0.08f);
            float h = getWidth() * 0.455f;
            float k = getHeight() * 0.44f;

            //Computes child position on screen
            child.setX((h + (a * FloatMath.cos(angle))));
            child.setY((k + (b * FloatMath.sin(angle))));

            //Sets view to the size of the child
            child.layout((int)(child.getX() - (childWidth * 0.5f)), (int)(child.getY() - (childHeight * 0.5f)),
                    (int)(child.getX() + (childWidth * 0.5f)), (int)(child.getY() + (childHeight * 0.5f)));
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        Paint rectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectanglePaint.setColor(0xFF000000);
        rectanglePaint.setStyle(Paint.Style.FILL);

        Paint palettePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        palettePaint.setColor(0xFFB26B00);
        palettePaint.setStyle(Paint.Style.FILL);

        RectF rect = new RectF(0, 0, getWidth(), getHeight());

        canvas.drawRect(rect, rectanglePaint);
        canvas.drawOval(rect, palettePaint);

        mixButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (inMixMode == false) {
                        inMixMode = true;
                        mixButton.setBackgroundColor(Color.YELLOW);
                    } else {
                        inMixMode = false;
                        mixButton.setBackgroundColor(0xFFDB944D);
                    }
                }
                return false;
            }
        });

        removeButton.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                removeColor(selectedColor);
                return false;
            }
        });
    }



    public void addColor(int color)
    {
        boolean colorExists = false;
        for(PaintView paint : allPaintViews)
        {
            if(paint.getColor() == color)
                colorExists = true;
        }

        final PaintView newPaint = new PaintView(context, color, allPaintViews);
        if(colorExists == false)
        {
            addView(newPaint);
            newPaint.setSelected(newPaint);
            selectedColor = newPaint.getColor();
            allPaintViews.add(newPaint);
            onLayout(true, 0, 0, 0, 0);
        }

        newPaint.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    PaintView temp = (PaintView) view;
                    if(inMixMode == true)
                    {
                        mixColors(temp.getColor());
                        inMixMode = false;
                        mixButton.setBackgroundColor(0xFFDB944D);
                    }
                    else
                    {
                        selectedColor = temp.getColor();
                        temp.setSelected(temp);
                    }

                    if(onColorChangedListener != null)
                        onColorChangedListener.getSelectedColor(selectedColor);
                }
                else
                {
                    return false;
                }
                return true;
            }
        });
    }

    public void removeColor(int color)
    {
        for(PaintView paint : allPaintViews)
        {
            int current = paint.getColor();
            if(color == Color.BLACK || color == Color.WHITE || color == Color.BLUE ||
                    color == Color.GREEN || color == Color.RED)
            {
                break;
            }

            if(paint.getColor() == color)
            {
                if(paint.getSelected() == paint)
                {
                    paint.setSelected(allPaintViews.get(4));
                    selectedColor = allPaintViews.get(4).getColor();
                }

                removeView(paint);
                allPaintViews.remove(paint);
                if(onColorChangedListener != null)
                    onColorChangedListener.getSelectedColor(selectedColor);
            }
        }
        onLayout(true, 0, 0, 0, 0);

    }

    public void mixColors(int secondColor)
    {
        int newRed;
        int newGreen;
        int newBlue;

        newRed = (Color.red(selectedColor) + Color.red(secondColor)) / 2;
        newGreen = (Color.green(selectedColor) + Color.green(secondColor)) / 2;
        newBlue = (Color.blue(selectedColor) + Color.blue(secondColor)) / 2;

        selectedColor = Color.rgb(newRed, newGreen, newBlue);
        addColor(selectedColor);
    }
}
