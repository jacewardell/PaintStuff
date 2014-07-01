package com.dubes33.paintstuff;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PaintActivity extends Activity
{
    ArrayList<Float> vertices = new ArrayList<Float>();
    public int selectedColor = 0xFF000000;
    static final int colorResult = 1;
    boolean inCreateMode = true;
    boolean isPlaying = false;
    double percentBegin;
    double percentEnd;
    Timer timer = new Timer();
    PaintAreaView paintAreaView;
    SideMenuView sideMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FrameLayout mainLayout = new FrameLayout(this);

        paintAreaView = new PaintAreaView(this, vertices);

        paintAreaView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    vertices.add(event.getX());
                    vertices.add(event.getY());
                    vertices.add((float) selectedColor);
                    paintAreaView.showDrawing(vertices, 0, 1.0);
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    vertices.add(event.getX());
                    vertices.add(event.getY());
                    vertices.add((float) selectedColor);
                    paintAreaView.showDrawing(vertices, 0, 1.0);
                }
                return true;
            }
        });

        sideMenuView = new SideMenuView(this);

        sideMenuView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                PointF touchPoint = new PointF(event.getX(), event.getY());
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if(Math.abs(touchPoint.x - sideMenuView.getScrubPosition()) < sideMenuView.getHeight() * 0.30f)
                    {
                        sideMenuView.setScrubSelected(true);
                        sideMenuView.invalidate();
                    }
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    if(Math.abs(touchPoint.x - sideMenuView.getScrubPosition()) < sideMenuView.getHeight() * 0.30f)
                    {
                        float percentage = (event.getX() - sideMenuView.getWidth() * 0.05f) / sideMenuView.scrubLength;
                        sideMenuView.setScrubPosition(event.getX());
                        paintAreaView.showDrawing(vertices, 0, percentage);
                    }
                }
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    sideMenuView.setScrubSelected(false);
                    sideMenuView.invalidate();
                }
                return true;
            }
        });


        int bWidth = (int)(sideMenuView.getWidth() * 0.30f);
        int bHeight = (int)(sideMenuView.getHeight() * 0.80f);

        final Button playPauseButton = new Button(this);
        playPauseButton.setBackgroundColor(Color.CYAN);
        playPauseButton.setText("Play");

        final Button createButton = new Button(this);
        createButton.setBackgroundColor(Color.GREEN);
        createButton.setText("Create");

        final Button paletteButton = new Button(this);
        paletteButton.setBackgroundColor(Color.BLACK);
        paletteButton.setText("Palette");


        final Button watchButton = new Button(this);
        watchButton.setBackgroundColor(Color.YELLOW);
        watchButton.setText("Watch");

        final Button clearButton = new Button(this);
        clearButton.setBackgroundColor(Color.WHITE);
        clearButton.setText("Clear");

        final Button rewindButton = new Button(this);
        rewindButton.setBackgroundColor(Color.MAGENTA);
        rewindButton.setText("Rewind");

        createButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                inCreateMode = true;
                paintAreaView.setEnabled(true);
                //Go into watch mode
                sideMenuView.removeAllViews();

                sideMenuView.addView(paletteButton);
                sideMenuView.addView(watchButton);
                sideMenuView.addView(clearButton);

                sideMenuView.setButtonColor(selectedColor);
                sideMenuView.setInCreateMode(inCreateMode);

                sideMenuView.onLayout(true, 0, 0, 0, 0);

                paintAreaView.showDrawing(vertices, 0, 1.0);

                return false;
            }
        });

        paletteButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    //Open the palette view activity
                    Intent intent = new Intent();
                    intent.setClass(PaintActivity.this, PaletteViewActivity.class);
                    startActivityForResult(intent, colorResult);

                }
                return false;
            }
        });
        sideMenuView.addView(paletteButton);

        watchButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    inCreateMode = false;
                    paintAreaView.setEnabled(false);
                    //Go into watch mode
                    sideMenuView.removeAllViews();

                    sideMenuView.addView(playPauseButton);
                    sideMenuView.addView(createButton);
                    sideMenuView.addView(rewindButton);

                    sideMenuView.setInCreateMode(inCreateMode);

                    sideMenuView.onLayout(true, 0, 0, 0, 0);
                }
                return false;
            }
        });
        sideMenuView.addView(watchButton);

        clearButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                vertices.clear();
                paintAreaView.showDrawing(vertices, 0, 1.0);
                return false;
            }
        });
        sideMenuView.addView(clearButton);

        playPauseButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    timer = new Timer();
                    percentBegin = percentEnd = 0;

                    if(playPauseButton.getText() == "Play")
                    {
                        isPlaying = true;
                        playPauseButton.setText("Pause");
                        createButton.setEnabled(false);
                        paintAreaView.playForward();

                        timer.scheduleAtFixedRate(new TimerTask()
                        {
                            @Override
                            public void run()
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if(percentEnd <= 1.0f)
                                        {
//                                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                                            percentage += (1 / (vertices.size() / 3));
                                            percentEnd += 0.0005f;
                                            paintAreaView.showDrawing(vertices, percentBegin,  percentEnd);
                                            sideMenuView.setScrubPosition((float) ((sideMenuView.scrubLength + sideMenuView.getWidth() * 0.05f) * percentEnd));
                                            percentBegin = percentEnd;
                                        }
                                        else
                                        {
                                            timer.cancel();
                                        }
                                    }
                                });
                            }
                        }, 0, 2);
                    }
                    else
                    {
                        //TODO: figure this out
                        timer.cancel();
                        isPlaying = false;
                        playPauseButton.setText("Play");
                        createButton.setEnabled(true);
                        paintAreaView.showDrawing(vertices, 0, percentEnd);
                    }
                }
                return true;
            }
        });

        rewindButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                final Timer timer = new Timer();
                percentBegin = percentEnd = 1.0f;

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if(playPauseButton.getText() == "Play")
                    {
                        isPlaying = true;
                        playPauseButton.setText("Pause");
                        createButton.setEnabled(false);
                        paintAreaView.playReverse();

                        timer.scheduleAtFixedRate(new TimerTask()
                        {
                            @Override
                            public void run()
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if(percentEnd >= 0.0f)
                                        {
//                                            percentage += (1 / (vertices.size() / 3));
                                            percentEnd -= 0.0005f;
                                            paintAreaView.showDrawing(vertices, percentBegin,  percentEnd);
                                            sideMenuView.setScrubPosition((float) ((sideMenuView.scrubLength + sideMenuView.getWidth() * 0.05f) * percentEnd));
                                            percentBegin = percentEnd;
                                        }
                                        else
                                            timer.cancel();
                                    }
                                });
                            }
                        }, 0, 2);
                    }
                }
                return false;
            }
        });

        paintAreaView.setId(9);
        sideMenuView.setId(8);

        mainLayout.addView(paintAreaView);
        mainLayout.addView(sideMenuView);

        setContentView(mainLayout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            selectedColor = data.getExtras().getInt("changedColor");

            paintAreaView.setActiveColor(selectedColor);
            sideMenuView.setButtonColor(selectedColor);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        ArrayList<Float> drawing = vertices;
        Boolean inCreateMode = sideMenuView.inCreateMode;

        try
        {
            FileOutputStream outputStream = openFileOutput("drawing.and", MODE_PRIVATE);
            ObjectOutputStream writer = new ObjectOutputStream(outputStream);
            writer.writeObject(drawing);

            outputStream = openFileOutput("inCreateMode.and", MODE_PRIVATE);
            writer = new ObjectOutputStream(outputStream);
            writer.writeObject(inCreateMode);

            outputStream = openFileOutput("selectedColor.and", MODE_PRIVATE);
            writer = new ObjectOutputStream(outputStream);
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
            FileInputStream inputStream = openFileInput("drawing.and");
            ObjectInputStream reader = new ObjectInputStream(inputStream);
            ArrayList<Float> drawing = (ArrayList<Float>) reader.readObject();

            inputStream = openFileInput("inCreateMode.and");
            reader = new ObjectInputStream(inputStream);
            Boolean inCreateMode = (Boolean) reader.readObject();

            inputStream = openFileInput("selectedColor.and");
            reader = new ObjectInputStream(inputStream);
            this.selectedColor = (Integer) reader.readObject();

            paintAreaView.showDrawing(drawing, 0, 1.0);
//            this.inCreateMode = inCreateMode;
//            if(selectedColor == 0xFF000000)
//                sideMenuView.setButtonColor(drawing.get(drawing.size() - 1));

//            if(inCreateMode == true)
//            {
//                final Button paletteButton = new Button(this);
//                paletteButton.setBackgroundColor(selectedColor);
//                sideMenuView.addView(paletteButton);
//
//                final Button watchButton = new Button(this);
//                watchButton.setBackgroundColor(Color.YELLOW);
//                watchButton.setText("Watch");
//
//                final Button clearButton = new Button(this);
//                clearButton.setBackgroundColor(Color.WHITE);
//                clearButton.setText("Clear");
//                sideMenuView.addView(clearButton);
//            }
//            else
//            {
//                final Button playPauseButton = new Button(this);
//                playPauseButton.setBackgroundColor(Color.CYAN);
//                playPauseButton.setText("Play");
//                sideMenuView.addView(playPauseButton);
//
//                final Button createButton = new Button(this);
//                createButton.setBackgroundColor(Color.GREEN);
//                createButton.setText("Create");
//                sideMenuView.addView(createButton);
//
//                final Button rewindButton = new Button(this);
//                rewindButton.setBackgroundColor(Color.MAGENTA);
//                rewindButton.setText("Rewind");
//                sideMenuView.addView(rewindButton);
//            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
