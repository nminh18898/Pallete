package com.nhatminh.example.pallete;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;
import androidx.palette.graphics.Target;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Integer> name = new ArrayList<>();

    RecyclerView rvImageList;
    LinearLayout parentLayout;

    ImageAdapter adapter;

    TextView vibrant, vibrantDark, vibrantLight, muted, mutedDark, mutedLight;
    TextView dominant, dark, light, neutral, customDominant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();

        createData();

        adapter = new ImageAdapter(this, name);
        rvImageList.setAdapter(adapter);


    }

    private void setupView(){
        parentLayout = findViewById(R.id.parentLayout);
        rvImageList = findViewById(R.id.rvImageList);
        rvImageList.setLayoutManager(new LinearLayoutManager(this));
        rvImageList.addOnItemTouchListener(new ImageListItemClickedListener(this, rvImageList, new ImageListItemClickedListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), name.get(position));
                createPaletteAsync(bitmap);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        vibrant = findViewById(R.id.vibrant);
        vibrantDark = findViewById(R.id.vibrantDark);
        vibrantLight = findViewById(R.id.vibrantLight);

        muted = findViewById(R.id.muted);
        mutedDark = findViewById(R.id.mutedDark);
        mutedLight = findViewById(R.id.mutedLight);

        dominant = findViewById(R.id.dominant);

        neutral = findViewById(R.id.neutral);

        customDominant = findViewById(R.id.customDominant);
    }

    public void createPaletteAsync(Bitmap bitmap) {
        new Palette.Builder(bitmap)
                .setRegion(0, (int) (bitmap.getHeight() * 0.8f), bitmap.getWidth(), bitmap.getHeight())
                //.setRegion(0, 0, bitmap.getWidth(), (int) (bitmap.getHeight() * 0.1))
                .addTarget(DOMINANT)
                .addTarget(NEUTRAL).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@Nullable Palette p) {

                        findGradientDominantColors(p.getSwatches());

                        Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
                        Palette.Swatch mutedSwatch = p.getMutedSwatch();

                        Palette.Swatch vibrantDarkSwatch = p.getDarkVibrantSwatch();
                        Palette.Swatch mutedDarkSwatch = p.getDarkMutedSwatch();

                        Palette.Swatch vibrantLightSwatch = p.getLightVibrantSwatch();
                        Palette.Swatch mutedLightSwatch = p.getLightMutedSwatch();

                        // Palette.Swatch dominantSwatch = p.getSwatchForTarget(DOMINANT);

                        // palette api
                        Palette.Swatch dominantSwatch = p.getDominantSwatch();

                        // custom dominant
                        Palette.Swatch customDominantSwatch = p.getSwatchForTarget(DOMINANT);

                        Palette.Swatch neutralSwatch = p.getSwatchForTarget(NEUTRAL);

                        if (vibrantSwatch != null){
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(vibrantSwatch.getRgb()));
                            vibrant.setBackgroundColor(vibrantSwatch.getRgb());
                        }

                        if (mutedSwatch != null){
                            muted.setBackgroundColor(mutedSwatch.getRgb());
                        }

                        if (vibrantDarkSwatch != null){
                            vibrantDark.setBackgroundColor(vibrantDarkSwatch.getRgb());
                        }

                        if (mutedDarkSwatch != null){
                            mutedDark.setBackgroundColor(mutedDarkSwatch.getRgb());
                        }

                        if (vibrantLightSwatch != null){
                            vibrantLight.setBackgroundColor(vibrantLightSwatch.getRgb());
                        }

                        if (mutedLightSwatch != null){
                            mutedLight.setBackgroundColor(mutedLightSwatch.getRgb());
                        }

                        if (dominantSwatch != null){
                            dominant.setBackgroundColor(dominantSwatch.getRgb());
                            rvImageList.setBackgroundColor(dominantSwatch.getRgb());
                        }

                        if (customDominantSwatch != null){
                            customDominant.setBackgroundColor(customDominantSwatch.getRgb());
                        }


                        if (neutralSwatch != null){
                            neutral.setBackgroundColor(neutralSwatch.getRgb());
                        }

                    }
                });
    }

    private GradientDrawable findGradientDominantColors(List<Palette.Swatch> allSwatches){
        List<Palette.Swatch> swatches = new ArrayList<>();

        for(int i=0; i<allSwatches.size();i++){
            swatches.add(allSwatches.get(i));
        }

        Collections.sort(swatches, new SwatchComparator());

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {swatches.get(0).getRgb(),swatches.get(1).getRgb()});
        gd.setCornerRadius(0f);

        return gd;
    }

    private int findBlendDominantColors(List<Palette.Swatch> allSwatches){
        List<Palette.Swatch> swatches = new ArrayList<>();

        for(int i=0; i<allSwatches.size();i++){
            swatches.add(allSwatches.get(i));
        }

        Collections.sort(swatches, new SwatchComparator());

        return interpolateColor(swatches.get(0).getRgb(), swatches.get(1).getRgb(),
                swatches.get(0).getPopulation() / swatches.get(1).getPopulation());
    }


    public static final Target DOMINANT;
    /*public static final Target DARK;
    public static final Target LIGHT;*/
    public static final Target NEUTRAL;

    static {
        DOMINANT = new Target.Builder().setPopulationWeight(1f)
                .setSaturationWeight(0f)
                .setLightnessWeight(0f)
                .setExclusive(true)
                .build();

        /*DARK = new Target.Builder().setMinimumLightness(0f)
                .setTargetLightness(0.26f)
                .setMaximumLightness(0.5f)
                .setMinimumSaturation(0.1f)
                .setTargetSaturation(0.6f)
                .setMaximumSaturation(1f)
                .setPopulationWeight(0.18f)
                .setSaturationWeight(0.22f)
                .setLightnessWeight(0.60f)
                .setExclusive(false)
                .build();

        LIGHT = new Target.Builder().setMinimumLightness(0.50f)
                .setTargetLightness(0.74f)
                .setMaximumLightness(1.0f)
                .setMinimumSaturation(0.1f)
                .setTargetSaturation(0.7f)
                .setMaximumSaturation(1f)
                .setPopulationWeight(0.18f)
                .setSaturationWeight(0.22f)
                .setLightnessWeight(0.60f)
                .setExclusive(false)
                .build();*/

        //
        NEUTRAL = new Target.Builder().setMinimumLightness(0.30f)
                .setTargetLightness(0.5f)
                .setMaximumLightness(0.7f)

                .setMinimumSaturation(0.35f)
                .setTargetSaturation(0.65f)
                .setMaximumSaturation(1f)

                .setPopulationWeight(0.24f)
                .setSaturationWeight(0.24f)
                .setLightnessWeight(0.60f)

                .setExclusive(true)
                .build();
    }


    private float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }

    private int interpolateColor(int a, int b, float proportion) {

        if (proportion > 1 || proportion < 0) {
            throw new IllegalArgumentException("proportion must be [0 - 1]");
        }
        float[] hsva = new float[3];
        float[] hsvb = new float[3];
        float[] hsv_output = new float[3];

        Color.colorToHSV(a, hsva);
        Color.colorToHSV(b, hsvb);
        for (int i = 0; i < 3; i++) {
            hsv_output[i] = interpolate(hsva[i], hsvb[i], proportion);
        }

        int alpha_a = Color.alpha(a);
        int alpha_b = Color.alpha(b);
        float alpha_output = interpolate(alpha_a, alpha_b, proportion);

        return Color.HSVToColor((int) alpha_output, hsv_output);
    }

    private class SwatchComparator implements Comparator<Palette.Swatch>{

        @Override
        public int compare(Palette.Swatch o1, Palette.Swatch o2) {
            return o2.getPopulation() - o1.getPopulation();
        }
    }

    private void createData(){
        name.add(R.drawable.image1);
        name.add(R.drawable.image2);
        name.add(R.drawable.image3);
        name.add(R.drawable.image4);
        name.add(R.drawable.image5);
        name.add(R.drawable.image6);
        name.add(R.drawable.image7);
        name.add(R.drawable.image8);

        /*name.add(R.drawable.image9);
        name.add(R.drawable.test);
        name.add(R.drawable.image20);
        name.add(R.drawable.image15);
        name.add(R.drawable.image15_bot);

        /*name.add(R.drawable.image51);
        name.add(R.drawable.image50);
        name.add(R.drawable.image55);*/
    }
}
