package com.dubes33.paintstuff;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Jace Wardell on 10/9/13.
 */
public class PaletteViewActivity extends Activity
{
    PaletteView paletteView;
    int selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        paletteView = new PaletteView(this);

        Button mix = new Button(this);
        mix.setText("Mix");
        mix.setBackgroundColor(0xFFDB944D);
        paletteView.addView(mix);

        Button remove = new Button(this);
        remove.setText("Remove");
        remove.setBackgroundColor(0xFFDB944D);
        paletteView.addView(remove);

        paletteView.addColor(Color.RED);
        paletteView.addColor(Color.GREEN);
        paletteView.addColor(Color.BLUE);
        paletteView.addColor(Color.WHITE);
        paletteView.addColor(Color.BLACK);

        paletteView.setOnColorChangedListener(new PaletteView.OnColorChangedListener()
        {
            @Override
            public void getSelectedColor(int color)
            {
                selectedColor = color;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("changedColor", color);
                // TODO Add extras or a data URI to this intent as appropriate.
                setResult(Activity.RESULT_OK, resultIntent);
            }
        });

        paletteView.setId(8);

        setContentView(paletteView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        ArrayList<Integer> allColors = new ArrayList<Integer>();
        for(PaintView paint : paletteView.allPaintViews)
        {
            allColors.add(paint.getColor());
        }

        try
        {
            FileOutputStream outputStream = openFileOutput("paints.and", MODE_PRIVATE);
            ObjectOutputStream writer = new ObjectOutputStream(outputStream);
            writer.writeObject(allColors);

            outputStream = openFileOutput("selectedColor.and", MODE_PRIVATE);
            writer = new ObjectOutputStream((outputStream));
            writer.writeObject(selectedColor);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        try
        {
            FileInputStream inputStream = openFileInput("paints.and");
            ObjectInputStream reader = new ObjectInputStream(inputStream);
            ArrayList<Integer> allColors = (ArrayList<Integer>) reader.readObject();

            for(Integer color : allColors)
            {
                paletteView.addColor(color);
            }

            paletteView.onColorChangedListener.getSelectedColor(selectedColor);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
