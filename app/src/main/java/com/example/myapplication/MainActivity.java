

package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    TextView result;
    TextView info;
    ImageView imageView;
    Button picture;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);
        info=findViewById(R.id.info);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
    }

    public void classify(Bitmap image){try {
        Model model = Model.newInstance(getApplicationContext());

        // Creates inputs for reference.
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
        ByteBuffer byteBuffer=ByteBuffer.allocateDirect(4*imageSize*imageSize*3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] val=new int[imageSize*imageSize];
        image.getPixels(val,0,image.getWidth(),0,0,image.getWidth(),image.getHeight());
        int pixel=0;
        for(int i=0;i<imageSize;i++)
        {
            for(int j=0;j<imageSize;j++){
                int vals=val[pixel++];
                byteBuffer.putFloat(((vals>>16)& 0xFF)*(1.f/255.f));
                byteBuffer.putFloat(((vals>>8)& 0xFF)*(1.f/255.f));
                byteBuffer.putFloat((vals& 0xFF)*(1.f/255.f));

            }
        }
        inputFeature0.loadBuffer(byteBuffer);
        // Runs model inference and gets result.
        Model.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
        float[] con=outputFeature0.getFloatArray();
        int maxpos=0;
        float maxcon=0;
        for(int i=0;i<con.length;i++){
            if(con[i]>maxcon){
                maxcon=con[i];
                maxpos=i;
            }
        }
    String []classes={"Organic waste","Medical Waste","Hazardous Waste","Paper","Plastic","Metal","Plastic(NR)"};


        String t=" ";
        if(maxpos==0){
            t="\n\n\tSome methods to handle such waste\n\n\t1)Creative Use of Leftovers.\n\t2)Donate Waste for Animal Feed.\n\t3)Convert Food Scrap into Biogas. \n\t4)Reuse the Food Packaging Material.";

        }
        if(maxpos==1){
            String col="#FF0000";
            t="\n\n\tSome methods to handle such waste\n\n\t1)Separate: Keep medical waste separate.\n2)Sharps: Use proper containers for needles.\n3)Meds: Dispose wisely or in take-back programs.\n4)Bag: Double-bag fluid-contaminated items.";

        }
        if(maxpos==2){

            t="\n\n\tSome methods to handle such waste\n\n\t1)Separation: Keep hazardous waste separate from regular trash.\n2)Labeling: Clearly mark containers as Hazardous Waste.\n\t3)Storage: Store hazardous waste in a cool, dry, and secure place.\n\t4)Leak Prevention: Ensure containers are sealed and won't leak..";

        }
        if(maxpos==3){
            t="\n\n\tSome methods to handle such waste\n\n\t1)Separation: Keep hazardous waste separate from regular trash.\n2)Labeling: Clearly mark containers as Hazardous Waste.\n\t3)Storage: Store hazardous waste in a cool, dry, and secure place.\n\t4)Leak Prevention: Ensure containers are sealed and won't leak..";

        }
        if(maxpos==4){
            t="\n\n\tSome methods to handle such waste\n\n\t1)Reduce: Minimize paper usage by going digital for bills, documents, and notes.\n\t2)Reuse: Utilize the back of printed papers for notes or drafts.\n\t3)Recycle: Separate clean paper from trash for recycling.\n\t4)Shredding: Shred sensitive documents before recycling.";
        }
        if(maxpos==5){
            t="\n\n\tSome methods to handle such waste\n\n\t1)Separation: Keep metal waste separate from regular trash.\n\t2)Sorting: Separate different types of metals (aluminum, steel, etc.).\n\t3)Containers: Use bins or bags to collect and store metal waste.\n\t4)Recycling: Check local recycling guidelines for metal collection";
        }
        if(maxpos==6){
            t="\n\n\tNot all plastic is recyclable\n\tObjects made by materials like PVC cannot be recycled.\n\tThis also includes chips packets and chocolates wrappers";   }
        result.setText(classes[maxpos]);
        info.setText(t);

        // Releases model resources if no longer used.
        model.close();
    } catch (IOException e) {
        // TODO Handle the exception
    }


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image=(Bitmap) data.getExtras().get("data");
            int dim=Math.min(image.getWidth(),image.getHeight());
            image=ThumbnailUtils.extractThumbnail(image,dim,dim);
            imageView.setImageBitmap(image);
            image=Bitmap.createScaledBitmap(image,imageSize,imageSize,false);

        classify(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}