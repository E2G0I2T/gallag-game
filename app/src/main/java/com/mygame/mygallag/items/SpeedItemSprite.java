package com.mygame.mygallag.items;


import android.content.Context;

import com.mygame.mygallag.R;
import com.mygame.mygallag.SpaceInvadersView;
import com.mygame.mygallag.sprites.Sprite;

import java.util.Timer;
import java.util.TimerTask;

public class SpeedItemSprite extends Sprite {

    SpaceInvadersView game;
    public SpeedItemSprite(Context context, SpaceInvadersView game, int x, int y, int dx, int dy) {
        super(context, R.drawable.speed_item, x, y);
        this.game = game;
        this.dx = dx;
        this.dy = dy;
        // dx , dy : Sprite 에서 Item 이동 속도를 가져옴

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                autoRemove();
            }
        },  15000); // 15초 후에 autoRemove() 를 호출해서 이 아이템을 없앰
    }

    private void autoRemove() {game.removeSprite(this);}
    // SpaceInvadersView 의 removeSprite 를 호출해서 아이템을 없앰
    @Override
    public void move() { // 아이템이 가장자리에 부딪히면 아이템 이동 방향을 바꿈
        if((dx < 0) && (x < 120)){
            dx *= -1;
            return;
        }
        if((dx > 0) && (x > game.screenW - 120)){
            dx *= -1;
            return;
        }
        if((dy < 0) && (y < 120)){
            dy *= -1;
            return;
        }
        if((dy > 0) && (y > game.screenH - 120)){
            dy *= -1;
            return;
        }
        super.move();
    }
}
