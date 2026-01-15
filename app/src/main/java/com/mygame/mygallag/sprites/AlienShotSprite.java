package com.mygame.mygallag.sprites;

import android.content.Context;

import com.mygame.mygallag.R;
import com.mygame.mygallag.SpaceInvadersView;

public class AlienShotSprite extends Sprite{
    private Context context;
    private SpaceInvadersView game;
    public AlienShotSprite(Context context, SpaceInvadersView game, float x, float y, int dy) {
        super(context, R.drawable.shot_001, x, y);
        this.game = game;
        this.context = context;
        setDy(dy); // 총알은 상하로만 움직이기 때문에 dy 만 설정
    }
}
