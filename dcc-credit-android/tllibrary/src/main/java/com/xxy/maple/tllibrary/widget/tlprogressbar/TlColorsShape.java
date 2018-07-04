package com.xxy.maple.tllibrary.widget.tlprogressbar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;

/**
 * Created by castorflex on 3/5/14.
 */
public class TlColorsShape extends Shape {

  private float mStrokeWidth;
  private int[] mColors;

  public TlColorsShape(float strokeWidth, int[] colors) {
    mStrokeWidth = strokeWidth;
    mColors = colors;
  }

  public float getStrokeWidth() {
    return mStrokeWidth;
  }

  public void setStrokeWidth(float strokeWidth) {
    mStrokeWidth = strokeWidth;
  }

  public int[] getColors() {
    return mColors;
  }

  public void setColors(int[] colors) {
    mColors = colors;
  }

  @Override
  public void draw(Canvas canvas, Paint paint) {
    float ratio = 1f / mColors.length;
    int i = 0;
    paint.setStrokeWidth(mStrokeWidth);
    for (int color : mColors) {
      paint.setColor(color);
      canvas.drawLine(i * ratio * getWidth(), getHeight() / 2, ++i * ratio * getWidth(), getHeight() / 2, paint);
    }
  }
}
