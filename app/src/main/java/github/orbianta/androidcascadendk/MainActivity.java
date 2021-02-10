package github.orbianta.androidcascadendk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("app");
    }

    //UI definitions
    private JavaCameraView ocv_camera;
    private ImageView camera_surface, output;
    private Button run_btn;

    //Native methods
    private native void detect_faces(long image_addr, String cascade_path);

    //Others
    public Boolean is_in_proc = false;
    public String cascade_path = new String();

    private void def_ui(){

        ocv_camera = findViewById(R.id.ocv_camera);
        camera_surface = findViewById(R.id.camera_surface);
        output = findViewById(R.id.output);
        run_btn = findViewById(R.id.run_btn);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        def_ui();

        AssetManager assetManager = getResources().getAssets();

        try {
            InputStream inputStream = new BufferedInputStream(assetManager.open("face.xml"));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();

            File file = new File(getFilesDir(), "face.xml");
            FileOutputStream os = new FileOutputStream(file);
            os.write(data);
            os.close();

             cascade_path = file.getAbsolutePath();
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("Error","Picking assets file failed. Closing app...");
            System.exit(0);
        }

        run_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cascade_path!=""){
                    if(!is_in_proc) is_in_proc = true;
                    else is_in_proc = false;
                }


            }
        });







    }


    public void onResume(){
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.e("Error","OpenCV Java side failed to load.");
            System.exit(0);

        } else {
            Log.i("Info","OpenCV Java side loaded.");
            watch_camera();

        }
    }

    private void watch_camera(){



        ocv_camera.enableView();

        ocv_camera.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener() {
            @Override
            public void onCameraViewStarted(int width, int height) {

            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(Mat inputFrame) {

                Imgproc.cvtColor(inputFrame,inputFrame,Imgproc.COLOR_RGBA2RGB);

                Bitmap view_bmp = Bitmap.createBitmap(inputFrame.cols(), inputFrame.rows(), Bitmap.Config.RGB_565);

                if(is_in_proc){

                    is_in_proc = false;



                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            detect_faces(inputFrame.getNativeObjAddr(),cascade_path);

                            Utils.matToBitmap(inputFrame,view_bmp);

                            output.setImageBitmap(view_bmp);

                            camera_surface.setImageBitmap(view_bmp);
                        }
                    });



                    return inputFrame;

                }

                Utils.matToBitmap(inputFrame,view_bmp);

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        camera_surface.setImageBitmap(view_bmp);
                    }
                });



                return inputFrame;
            }
        });

    }



}