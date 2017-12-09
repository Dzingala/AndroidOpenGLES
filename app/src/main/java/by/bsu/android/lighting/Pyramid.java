package by.bsu.android.lighting;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class Pyramid {

   //Geometry
    private FloatBuffer vertexBuffer;
    private final int POSITION_COUNT = 3; //number of position coordinates
    private final int TEXTURE_COORD_COUNT = 2;
    private final int COORDINATES_PER_VERTEX = POSITION_COUNT+TEXTURE_COORD_COUNT;
    private final int TRIANGLES_COUNT = 6;

    private static float pyramidCoords[] = {
            0.0f,  -0.8f,0.0f, 0.5f,1f,//1 triangle
            0.8f, 0.8f,0.8f, 0.95f,0f,
            -0.8f, 0.8f,0.8f, 0.05f,0f,

            0.0f,  -0.8f,0.0f, 0.5f,1f, //2
            -0.8f, 0.8f,0.8f, 0.95f,0f,
            -0.8f, 0.8f,-0.8f, 0.05f,0f,

            0.0f,  -0.8f,0.0f, 0.5f,1f, //3
            -0.8f, 0.8f,-0.8f, 0.95f,0f,
            0.8f, 0.8f,-0.8f, 0.05f,0f,

            0.0f,  -0.8f,0.0f, 0.5f,1f, //4
            0.8f, 0.8f,-0.8f, 0.95f,0f,
            0.8f, 0.8f,0.8f, 0.05f,0f,

            0.8f,  0.8f,0.8f, 1f,1f, //5
            -0.8f, 0.8f,-0.8f, 0f,0f,
            -0.8f, 0.8f,0.8f, 0.0f,1f,

            0.8f,  0.8f,0.8f, 1f,1f, //6
            0.8f, 0.8f,-0.8f, 1f,0f,
            -0.8f, 0.8f,-0.8f, 0f,0f
    };

    //Light source location
    private static float vectorToLight[] ={0f,10f,10f};



    //Rendering
    private final String vertexShaderCode =
                    "attribute vec4 aPosition;" +
                    "attribute vec2 aTexCoord;" +
                    "uniform mat4 uMVPMatrix;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "  vTexCoord = aTexCoord;" +
                    "  gl_Position = uMVPMatrix*aPosition;" +
                    "}";

    private final String fragmentShaderCode =
                    "precision mediump float;" +
                    "uniform float uCos;" +
                    "uniform sampler2D uTextureUnit;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(uTextureUnit,vTexCoord)*uCos;" +
                    "}";


    private final int mProgram;


    //Draw
    private int mPositionHandle;
    private int mTextCoordHandle;
    private int mCosHandle;
   // private int mColorHandle;
    private int mTextureUnit;
    private int mMVPMatrixHandle;
    private final int vertexStride = COORDINATES_PER_VERTEX * 4; // 4 bytes per vertex



    public Pyramid() {
        ByteBuffer bb = ByteBuffer.allocateDirect(pyramidCoords.length*4);//4 bytes for 1 float
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(pyramidCoords);

        int vertexShader = CustomGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = CustomGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix,int[] textureData){
        GLES20.glUseProgram(mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTextCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");

        mTextureUnit = GLES20.glGetUniformLocation(mProgram, "uTextureUnit");
        mCosHandle = GLES20.glGetUniformLocation(mProgram, "uCos");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1,false,mvpMatrix,0);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, POSITION_COUNT,
                GLES20.GL_FLOAT, false,vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        vertexBuffer.position(POSITION_COUNT);
        GLES20.glVertexAttribPointer(mTextCoordHandle,TEXTURE_COORD_COUNT,
                GLES20.GL_FLOAT, false,vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mTextCoordHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        for(int j=0;j<TRIANGLES_COUNT;j++){

            //Taking 3 points A(x1,y1,z1), B(x2,y2,z2),C(x3,y3,z3)
            //Calculating a normal vector N(nx,ny,nz)
            int i = j*COORDINATES_PER_VERTEX*3;

            float x1 = pyramidCoords[i];
            float y1 = pyramidCoords[i+1];
            float z1 = pyramidCoords[i+2];

            float x2 = pyramidCoords[i+COORDINATES_PER_VERTEX];
            float y2 = pyramidCoords[i+1+COORDINATES_PER_VERTEX];
            float z2 = pyramidCoords[i+2+COORDINATES_PER_VERTEX];

            float x3 = pyramidCoords[i+2*COORDINATES_PER_VERTEX];
            float y3 = pyramidCoords[i+1+2*COORDINATES_PER_VERTEX];
            float z3 = pyramidCoords[i+2+2*COORDINATES_PER_VERTEX];

            float nx =y1*(z2 - z3) + y2*(z3 - z1) + y3*(z1 - z2);
            float ny =z1*(x2 - x3) + z2*(x3 - x1) + z3*(x1 - x2);
            float nz =x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2);

            float lengthN = (float) Math.sqrt(nx*nx+ny*ny+nz*nz);
            nx= nx/lengthN;
            ny= ny/lengthN;
            nz= nz/lengthN;

            //Normalizing vectorToLight
            float lengthTL = (float) Math.sqrt(vectorToLight[0]*vectorToLight[0]+
                    vectorToLight[1]*vectorToLight[1]+vectorToLight[2]*vectorToLight[2]);
            vectorToLight[0] = vectorToLight[0]/lengthTL;
            vectorToLight[1] = vectorToLight[1]/lengthTL;
            vectorToLight[2] = vectorToLight[2]/lengthTL;

            //Calculating cos between normal and light source vectors
            float cos_value = nx*vectorToLight[0]+ny*vectorToLight[1]+nz*vectorToLight[2];
            if(cos_value<0) cos_value = 0.0f;

            GLES20.glUniform1f(mCosHandle,cos_value);
           // GLES20.glUniform1f(mCosHandle,1f);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureData[j]);
            GLES20.glUniform1i(mTextureUnit,0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, j*3, 3); //3 vertices for every triangle

        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextCoordHandle);
    }
}
