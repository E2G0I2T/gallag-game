package com.mygame.mygallag.sprites;


import android.content.Context;

import com.mygame.mygallag.SpaceInvadersView;

public class ShotSprite extends Sprite{
    private SpaceInvadersView game;

    public ShotSprite(Context context, SpaceInvadersView game, int resId, float x, float y, int dy) {
        super(context, resId, x, y);
        this.game = game;
        setDy(dy); // 총알은 상하로만 움직이기 때문에 dy 속도만 설정
    }
}
