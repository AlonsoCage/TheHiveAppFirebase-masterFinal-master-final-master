

package com.alonsocage.componentes.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import com.alonsocage.componentes.R;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashActivity extends BaseActivity implements Animation.AnimationListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();


        View myImage = findViewById(R.id.imageView);
        TextView titulo = (TextView) findViewById(R.id.Title);

        Animation animaImagen = AnimationUtils.loadAnimation(this, R.anim.fadein);
        Animation animaImagen2 = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
        Animation animaTitulo = AnimationUtils.loadAnimation(this, R.anim.leftin);


        Typeface myFont = Typeface.createFromAsset(getAssets(), "AmaticSC-Regular.ttf");
        titulo.setTypeface(myFont);



        myImage.startAnimation(animaImagen);
        myImage.startAnimation(animaImagen2);
        titulo.startAnimation(animaTitulo);
        animaTitulo.setAnimationListener(this);

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }


    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
