package com.amaizzzing.amaizingweather.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.amaizzzing.amaizingweather.R;

public class ThermometerView extends View {
    private final static int padding = 10;
    private final static int round = 70;
    private final static int headHeight = 20;

    private RectF thermometerRectangle = new RectF();
    private RectF thermometerRectangle2 = new RectF();
    private RectF levelRectangle = new RectF();
    private Paint thermometerPaint;
    private Paint thermometerPaint2;
    private Paint levelPaint;
    private Paint textPaint;
    private Paint linePaint;
    private int width = 0;
    private int height = 0;

    private int thermometer_color = Color.WHITE;
    private int level_color = getResources().getColor(R.color.transGrey);
    private int degrees = 25;

    private float partOfFullHeight = 0;

    ThermometrHeight thermometrHeight;

    public ThermometerView(Context context) {
        super(context);
        init();
    }

    public ThermometerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public ThermometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    public ThermometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        init();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThermometerView, 0, 0);
        thermometer_color = typedArray.getColor(R.styleable.ThermometerView_thermometer_color, Color.GRAY);
        level_color = typedArray.getColor(R.styleable.ThermometerView_level_color, getResources().getColor(R.color.colorPrimaryDark));
        degrees = typedArray.getInteger(R.styleable.ThermometerView_degrees, 0);
        typedArray.recycle();
    }

    private void init() {
        thermometrHeight = new ThermometrHeight(height, partOfFullHeight, degrees);

        thermometerPaint = new Paint();
        thermometerPaint.setColor(thermometer_color);
        thermometerPaint.setStyle(Paint.Style.FILL);

        thermometerPaint2 = new Paint();
        thermometerPaint2.setColor(getResources().getColor(R.color.transGrey));
        thermometerPaint2.setStyle(Paint.Style.FILL);

        levelPaint = new Paint();
        levelPaint.setColor(level_color);
        levelPaint.setStyle(Paint.Style.FILL);

        linePaint = new Paint();
        linePaint.setStrokeWidth(3);
        levelPaint.setColor(level_color);
        levelPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(22f);
        textPaint.setStyle(Paint.Style.FILL);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w - getPaddingLeft() - getPaddingRight();
        height = h - getPaddingTop() - getPaddingBottom();

        partOfFullHeight = (height) / ThermometrHeight.COUNT_PARTS_THERMOMETR * 1f;

        thermometerRectangle.set(padding,
                padding,
                width - padding - headHeight,
                height - padding);
        thermometerRectangle2.set(padding + 10,
                padding + 10,
                width - padding - headHeight - 10,
                height - padding - 10);
        thermometrHeight = new ThermometrHeight(height, partOfFullHeight, degrees);
        setLevelRectangle(degrees);
        textPaint.setTextSize(width * height / 5000f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(thermometerRectangle, round, round, thermometerPaint);
        canvas.drawRoundRect(thermometerRectangle2, round, round, thermometerPaint2);
        if(degrees>=0){
            levelPaint.setColor(Color.RED);
        }else{
            levelPaint.setColor(Color.BLUE);
        }
        canvas.drawRoundRect(levelRectangle, 10, 10, levelPaint);
        thermometrHeight.drawLinesAndTexts(canvas, width, padding, textPaint, linePaint);
    }

    public void setDegrees(int degrees) {
        this.degrees = degrees;
        thermometrHeight = new ThermometrHeight(height, partOfFullHeight, degrees);
        setLevelRectangle(degrees);
        invalidate();
    }

    private void setLevelRectangle(int degrees) {
        levelRectangle.set(width / 2f - 15,
                (int) thermometrHeight.getTemperatureHeight(),
                width / 2f - 5,
                height - 2.0f * padding);
    }

}
