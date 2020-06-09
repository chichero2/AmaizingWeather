package com.amaizzzing.amaizingweather.custom_view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class ThermometrHeight {
    final static float COUNT_PARTS_THERMOMETR = 9f;
    private final static int MAX_DEGREES_TEXT = 40;
    private static final int STEP_DEGREES_TEXT = 10;

    private final static float[] ARRAY_PARTS_TERMOMETR = {
            COUNT_PARTS_THERMOMETR / 6.3f,
            COUNT_PARTS_THERMOMETR / 4.1f,
            COUNT_PARTS_THERMOMETR / 3f,
            COUNT_PARTS_THERMOMETR / 2.4f,
            COUNT_PARTS_THERMOMETR / 2f,
            COUNT_PARTS_THERMOMETR / 1.73f,
            COUNT_PARTS_THERMOMETR / 1.5f,
            COUNT_PARTS_THERMOMETR / 1.33f,
            COUNT_PARTS_THERMOMETR / 1.2f
    };

    private float height;
    private float partOfFullHeight;
    private int degr;

    ThermometrHeight(float height, float partOfFullHeight, int degrees) {
        this.height = height;
        this.partOfFullHeight = partOfFullHeight;
        this.degr = degrees;
    }

    float getTemperatureHeight() {//не додумался, как эти if оптимизировать
        if (degr == 0) {
            return height / 2f;
        }
        if (degr > 40) {
            return partOfFullHeight / 2f;
        }
        if (degr < -40) {
            return height - partOfFullHeight;
        }
        if (degr > 30 && degr <= 40) {
            return getResultHeight(ARRAY_PARTS_TERMOMETR[1]);
        }
        if (degr > 20 && degr <= 30) {
            return getResultHeight(ARRAY_PARTS_TERMOMETR[2]);
        }
        if (degr > 10 && degr <= 20) {
            return getResultHeight(ARRAY_PARTS_TERMOMETR[3]);
        }
        if (degr > 0 && degr <= 10) {
            return getResultHeight(ARRAY_PARTS_TERMOMETR[4]);
        }
        if (degr < 0 && degr >= -10) {
            return getResultHeight(ARRAY_PARTS_TERMOMETR[5]);
        }
        if (degr < -10 && degr >= -20) {
            return getResultHeight(ARRAY_PARTS_TERMOMETR[6]);
        }
        if (degr < -20 && degr >= -30) {
            return getResultHeight(ARRAY_PARTS_TERMOMETR[7]);
        }
        if (degr < -30 && degr >= -40) {
            return getResultHeight(ARRAY_PARTS_TERMOMETR[8]);
        }
        return 0;
    }

    private float getResultHeight(float arrayPartsTerm) {
        int degrees = degr;
        float number2;
        int number1 = (int) myRound10abs(degrees);
        if (degrees > 0) {
            degrees = -degrees;
            number2 = 1.25f;
        } else {
            number2 = 10f;
        }
        if (degrees % (COUNT_PARTS_THERMOMETR + 1) != 0) {
            return partOfFullHeight * arrayPartsTerm - partOfFullHeight / ((Math.abs(degrees + number1) % (COUNT_PARTS_THERMOMETR + 1)) + 0.5f);
        } else {
            return partOfFullHeight * arrayPartsTerm - partOfFullHeight / number2;
        }
    }

    void drawLinesAndTexts(Canvas canvas, int width, int padding, Paint textPaint, Paint LinePaint) {
        int textDegrees = MAX_DEGREES_TEXT;
        for (int i = 0; i < ARRAY_PARTS_TERMOMETR.length; i++) {
            if (textDegrees >= 0) {
                canvas.drawLine(width / 2f, partOfFullHeight * ARRAY_PARTS_TERMOMETR[i], width - padding * 4, partOfFullHeight * ARRAY_PARTS_TERMOMETR[i], LinePaint);
                textPaint.setColor(Color.RED);
                canvas.drawText(String.valueOf(textDegrees), 5 + width / 2f, partOfFullHeight * ARRAY_PARTS_TERMOMETR[i] - 3, textPaint);
            } else {
                canvas.drawLine(padding * 2, partOfFullHeight * ARRAY_PARTS_TERMOMETR[i], width / 2f - padding * 2, partOfFullHeight * ARRAY_PARTS_TERMOMETR[i], LinePaint);
                textPaint.setColor(Color.BLUE);
                canvas.drawText(String.valueOf(textDegrees), width / 6f, partOfFullHeight * ARRAY_PARTS_TERMOMETR[i] - 3, textPaint);
            }
            textDegrees = textDegrees - STEP_DEGREES_TEXT;
        }
    }

    private double myRound10abs(float number) {
        if (number < 0) {
            if (number > -10) {
                return Math.floor(((Math.abs(number)) / 10)) * 10;

            } else {
                return Math.floor(((10 - Math.abs(number)) / 10) + 1) * 10;
            }
        } else {
            return Math.floor((Math.abs(number) / 10) + 1) * 10;
        }
    }
}
