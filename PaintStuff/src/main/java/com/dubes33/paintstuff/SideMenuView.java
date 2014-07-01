package com.dubes33.paintstuff;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Jace Wardell on 10/8/13.
 */
public class SideMenuView extends ViewGroup implements OnTouchListener
{
    public boolean inCreateMode = true;
    private Button paletteButton;
    private Button watchButton;
    private Button playPauseButton;
    private Button createButton;
    private Button clearButton;
    private Button rewindButton;
    private float selectedColor = 0xFF000000;
    private Context context;
    public float scrubLength;
    private boolean scrubSelected = false;
    private float scrubberX;
    private float scrubberY;

    public SideMenuView(Context context)
    {
        super(context);

        this.context = context;

        this.setOnTouchListener(this);

        this.setWillNotDraw(false);
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4)
    {
        int bWidth = (int)(getWidth() * 0.23f);
        int bHeight = (int)(getHeight() * 0.40f);

        if(inCreateMode == true)
        {
            inCreateMode = true;
            paletteButton = (Button) getChildAt(0);
            watchButton = (Button) getChildAt(1);
            clearButton = (Button) getChildAt(2);
        }
        else
        {
            inCreateMode = false;
            playPauseButton = (Button) getChildAt(0);
            createButton = (Button) getChildAt(1);
            rewindButton = (Button) getChildAt(2);
        }
        View button = getChildAt(0);

        button.setX(getWidth() * 0.85f - bWidth * 0.5f);
        button.setY(getHeight() * 0.25f - bHeight * 0.5f);

        //Sets view to the size of the child
        button.layout((int)(button.getX() - (bWidth * 0.5f)), (int)(button.getY() - (bHeight * 0.5f)),
        (int)(button.getX() + (bWidth * 0.5f)), (int)(button.getY() + (bHeight * 0.5f)));

        button = getChildAt(1);

        button.setX(getWidth() * 0.85f - bWidth * 0.5f);
        button.setY(getHeight() * 0.75f - bHeight * 0.5f);

        //Sets view to the size of the child
        button.layout((int)(button.getX() - (bWidth * 0.5f)), (int)(button.getY() - (bHeight * 0.5f)),
                (int)(button.getX() + (bWidth * 0.5f)), (int)(button.getY() + (bHeight * 0.5f)));

        button = getChildAt(2);
        button.setX(getWidth() * 0.85f - bWidth * 1.5f);
        button.setY(getHeight() * 0.25f - bHeight * 0.5f);

        //Sets view to the size of the child
        button.layout((int)(button.getX() - (bWidth * 0.5f)), (int)(button.getY() - (bHeight * 0.5f)),
                (int)(button.getX() + (bWidth * 0.5f)), (int)(button.getY() + (bHeight * 0.5f)));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        Paint menuPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        menuPaint.setColor(0xFF718EE8);
        menuPaint.setStyle(Paint.Style.FILL);

        RectF menuRect = new RectF(0, 0, getWidth(), getHeight());

        canvas.drawRect(menuRect, menuPaint);

        if(inCreateMode == false)
        {
            Paint scrubPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            scrubPaint.setColor(Color.DKGRAY);
            scrubPaint.setStrokeWidth(getHeight() * 0.10f);
            scrubPaint.setStyle(Paint.Style.FILL);

            canvas.drawLine(getWidth() * 0.05f, getHeight() * 0.75f, getWidth() * 0.65f, getHeight() * 0.75f, scrubPaint);

            scrubLength = (getWidth() * 0.65f) - (getWidth() * 0.05f);

            if(scrubSelected == true)
                scrubPaint.setColor(Color.YELLOW);
            else
                scrubPaint.setColor(Color.BLUE);

            canvas.drawCircle(scrubberX, scrubberY, Math.min(getWidth(), getHeight()) * 0.15f, scrubPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this.setMeasuredDimension(widthMeasureSpec, (int)(MeasureSpec.getSize(heightMeasureSpec) * 0.20f));
        this.setY(MeasureSpec.getSize(heightMeasureSpec) * 0.80f);

        scrubberX = getWidth() * 0.05f;
        scrubberY = getHeight() * 0.75f;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        return false;
    }

    public void setButtonColor(float color)
    {
        selectedColor = color;

        if(inCreateMode == true)
            paletteButton.setBackgroundColor((int)selectedColor);
    }

    public void setScrubPosition(float x)
    {
        if(x > getWidth() * 0.65f)
            scrubberX = getWidth() * 0.65f;
        else if(x < getWidth() * 0.05f)
            scrubberX = getWidth() * 0.05f;
        else
            scrubberX = x;
        invalidate();
    }

    public float getScrubPosition()
    {
        return scrubberX;
    }

    public void setScrubSelected(boolean b)
    {
        scrubSelected = b;
    }

    public boolean getScrubSelected()
    {
        return scrubSelected;
    }

    public void setInCreateMode(boolean inCreateMode)
    {
        this.inCreateMode = inCreateMode;
    }

//    @Override
//    protected Parcelable onSaveInstanceState()
//    {
//        Bundle bundle = new Bundle();
//
//        bundle.putParcelable("super", super.onSaveInstanceState());
//        bundle.putSerializable("createMode", inCreateMode);
//        bundle.putSerializable("selectedColor", selectedColor);
//
//        return bundle;
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Parcelable state)
//    {
//        if(state instanceof Bundle)
//        {
//            Bundle bundle = (Bundle) state;
//            super.onRestoreInstanceState(bundle.getParcelable("super"));
//            inCreateMode = (Boolean) bundle.getSerializable("createMode");
//
//            final Button playPauseButton = new Button(context);
//            playPauseButton.setBackgroundColor(Color.CYAN);
//            playPauseButton.setText("Play");
//            this.playPauseButton = playPauseButton;
//
//            final Button createButton = new Button(context);
//            createButton.setBackgroundColor(Color.GREEN);
//            createButton.setText("Create");
//            this.createButton = createButton;
//
//            final Button paletteButton = new Button(context);
//            paletteButton.setBackgroundColor(Color.BLACK);
//            this.paletteButton = paletteButton;
//
//            final Button watchButton = new Button(context);
//            watchButton.setBackgroundColor(Color.YELLOW);
//            watchButton.setText("Watch");
//            this.watchButton = watchButton;
//
//            final Button rewindButton = new Button(context);
//            rewindButton.setBackgroundColor(Color.MAGENTA);
//            rewindButton.setText("Rewind");
//            this.rewindButton = rewindButton;
//
//            if(inCreateMode == true)
//            {
//                addView(paletteButton);
//                addView(watchButton);
//
//                setButtonColor((Integer) bundle.getSerializable("selectedColor"));
//            }
//            else
//            {
//                addView(playPauseButton);
//                addView(watchButton);
//                addView(rewindButton);
//            }
//        }
//    }
}