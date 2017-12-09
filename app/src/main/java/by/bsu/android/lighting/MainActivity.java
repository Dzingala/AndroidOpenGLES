package by.bsu.android.lighting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity
                          implements SeekBar.OnSeekBarChangeListener {

    SeekBar seekScale, seekX, seekY, seekZ;

    CustomGLSurfaceView sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sw = new CustomGLSurfaceView(this);
        setContentView(sw);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.activity_main, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        addContentView(layout, params);

        seekScale = (SeekBar) findViewById(R.id.seekScale);
        seekScale.setProgress(5);
        seekScale.setOnSeekBarChangeListener(this);

        seekX = (SeekBar) findViewById(R.id.seekX);
        seekX.setProgress(180);
        seekX.setOnSeekBarChangeListener(this);

        seekY = (SeekBar) findViewById(R.id.seekY);
        seekY.setProgress(180);
        seekY.setOnSeekBarChangeListener(this);


        seekZ = (SeekBar) findViewById(R.id.seekZ);
        seekZ.setProgress(180);
        seekZ.setOnSeekBarChangeListener(this);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        final float rX = seekX.getProgress() - 180; //because 0<progress<180
        final float rY = seekY.getProgress() - 180; //because 0<progress<180
        final float rZ = seekZ.getProgress() - 180; //because 0<progress<180

        int scl = seekScale.getProgress();
        if (scl == 0) scl = 1;

        final float scale = scl;

        sw.queueEvent(new Runnable() {
            @Override
            public void run() {
                sw.getRenderer().onAction(rX, rY, rZ, scale);
            }
        });
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
