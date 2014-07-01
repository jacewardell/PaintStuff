package com.dubes33.paintstuff;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Jace on 9/20/13.
 */
public class PaintView extends View
{

    public int color;
    public ArrayList<PaintView> allPaintViews = new ArrayList<PaintView>();
    public PaintView selectedView;
    public Context context;

    public PaintView(Context context, int color, ArrayList<PaintView> allPaintViews)
    {
        super(context);

        this.color = color;
        this.context = context;

        if(color == Color.BLACK)
          setSelected(this);

        this.allPaintViews = allPaintViews;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        Paint paintView = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintView.setColor(color);
        paintView.setStyle(Paint.Style.FILL);

        canvas.drawCircle(getWidth() * 0.5f, getHeight() * 0.5f, getHeight() * 0.5f, paintView);
    }

    public int getColor()
    {
        return color;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public void setSelected(PaintView view)
    {
        selectedView = view;
        for(PaintView temp : allPaintViews)
        {
            temp.setBackgroundColor(Color.TRANSPARENT);
        }
        view.setBackgroundColor(Color.YELLOW);
    }

    public PaintView getSelected()
    {
        return selectedView;
    }
}
