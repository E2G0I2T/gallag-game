package com.mygame.mygallag.sprites;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Sprite {
    protected  float x, y;
    protected int width, height;
    protected float dx, dy;
    private Bitmap bitmap; // 이미지 데이터를 저장하고 조작하는 데 사용되는 클래스
    protected int id;
    private RectF rect; // 사각형을 만들때 사용하는 변수 ( 소수점 좌표를 사용하여 )
    public Sprite(Context context, int resourceId, float x, float y){
        this.id = resourceId;
        this.x = x;
        this.y = y;
        bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        rect = new RectF();
    }
    public int getWidth(){
        return width;
    } // 스프라이트의 너비 반환
    public int getHeight(){
        return height;
    } // 스프라이트의 높이 반환
    public void draw(Canvas canvas, Paint paint){canvas.drawBitmap(bitmap, x, y, paint);}
    // 스프라이트를 주어진 Canvas 에 그림
    public void move(){ // 스프라이트를 현재 이동 속도에 따라 이동
        x = x + dx;
        y = y + dy;
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y + height;
    }
    public float getX(){return x;} // 스프라이트의 좌표 반환
    public float getY(){return y;}
    public float getDx(){return dx;} // 스프라이트의 이동 속도 반환
    public float getDy(){return dy;}
    public void setDx(float dx){this.dx = dx;} // 스프라이트의 이동 속도를 설정
    public void setDy(float dy){this.dy = dy;}
    public RectF getRect(){return rect;} // 스프라이트의 충동 영역인 사각형을 그림, 반환
    public boolean checkCollision(Sprite other){
        // 다른 스프라이트와 충돌 확인, 두 스프라이트의 충돌 영역이 겹치는지 확인
        return RectF.intersects(this.getRect(), other.getRect());
    }
    public void handleCollision(Sprite other){} // 충돌 처리 위한 메서드


    public Bitmap getBitmap(){return bitmap;} // 현재 비트맵 이미지를 가져오거나 설정
    public void setBitmap(Bitmap bitmap){this.bitmap = bitmap;}
}
