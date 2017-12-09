package by.bsu.android.lighting;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class CustomGLSurfaceView extends GLSurfaceView {
    private final CustomGLRenderer mRenderer;

    public CustomGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        mRenderer = new CustomGLRenderer(context);
        setRenderer(mRenderer);
    }

    public CustomGLRenderer getRenderer(){
      return mRenderer;
    }

}
