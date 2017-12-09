package by.bsu.android.lighting;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



public class CustomGLRenderer implements GLSurfaceView.Renderer {

    Context context;
    private Pyramid pyramid;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];

    final int textureObjectsID[] = new int[8];




   public CustomGLRenderer(Context context){
       this.context = context;
   }


    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.9f,1f,0.9f,1f);
       pyramid = new Pyramid();

       GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        //load textures
        int textureID[] = new int[]{R.drawable.t1,R.drawable.t2,R.drawable.t3,R.drawable.t4,
                R.drawable.t5,R.drawable.t5};


        GLES20.glGenTextures(6,textureObjectsID,0);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled=false;

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        for(int i=0;i<6;i++){
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),textureID[i],options);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureObjectsID[i]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
            bitmap.recycle();
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0); //Unbind
        }
    }

    @Override
     public void onSurfaceChanged(GL10 gl10, int w, int h) {
        GLES20.glViewport(0, 0, w, h);

        float ratio = w > h ? (float) w / h : (float) h / w;

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setLookAtM(mViewMatrix, 0,
                0.0f, 1.5f, 3f,
                0f, 0f, 0f,
                0f, 1f, 0);
        Matrix.perspectiveM(mProjectionMatrix, 0, 45f, ratio, 0.1f, 10f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
    }

    public void onAction(float valueX,float valueY,float valueZ,float scale) {

        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.rotateM(mModelMatrix, 0, valueX, 1f, 0f, 0f);
        Matrix.rotateM(mModelMatrix, 0, valueY, 0f, 1f, 0f);
        Matrix.rotateM(mModelMatrix, 0, valueZ, 0f, 0f, 1f);

        Matrix.scaleM(mModelMatrix, 0, scale / 5.0f, scale / 5.0f, scale / 5.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        pyramid.draw(mMVPMatrix, textureObjectsID);
    }
}
