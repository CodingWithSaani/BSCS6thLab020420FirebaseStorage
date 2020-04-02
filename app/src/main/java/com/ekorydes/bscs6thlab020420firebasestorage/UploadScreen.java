package com.ekorydes.bscs6thlab020420firebasestorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadScreen extends AppCompatActivity {

    private ImageView imageToUploadIV;
    private EditText imageNameET;

    private Button imageUploadingBtn;
    private static final int REQUEST_CODE=124;

    private Uri imageDataInUriForm;
    private StorageReference objectStorageReference;

    private FirebaseFirestore objectFirebaseFirestore;
    private boolean isImageSelected=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_screen);

        objectFirebaseFirestore=FirebaseFirestore.getInstance();
        objectStorageReference= FirebaseStorage.getInstance().getReference("BSCSAImagesFolder");
        connectXMLToJava();
    }

    private void connectXMLToJava() {
        try {
            imageToUploadIV = findViewById(R.id.imageToUploadIV);
            imageNameET = findViewById(R.id.imageNameET);

            imageUploadingBtn = findViewById(R.id.imageUploadingBtn);
            imageToUploadIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openGallery();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "connectXMLToJava:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        try {
            Intent objectIntent = new Intent(); //Step 1:create the object of intent
            objectIntent.setAction(Intent.ACTION_GET_CONTENT); //Step 2: You want to get some data

            objectIntent.setType("image/*");//Step 3: Images of all type
            startActivityForResult(objectIntent,REQUEST_CODE);

        } catch (Exception e) {
            Toast.makeText(this, "openGallery:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            if(requestCode==REQUEST_CODE && resultCode==RESULT_OK && data!=null)
            {
                imageDataInUriForm=data.getData();
                Bitmap objectBitmap;

                objectBitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageDataInUriForm);
                imageToUploadIV.setImageBitmap(objectBitmap);

                isImageSelected=true;

            }
            else if(requestCode!=REQUEST_CODE)
            {
                Toast.makeText(this, "Request code doesn't match", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode!=RESULT_OK)
            {
                Toast.makeText(this, "Fails to get image", Toast.LENGTH_SHORT).show();
            }
            else if(data==null)
            {
                Toast.makeText(this, "No image was selected", Toast.LENGTH_SHORT).show();
            }


        }
        catch (Exception e) {
            Toast.makeText(this, "onActivityResult:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadOurImage()
    {
        try
        {
            if(imageDataInUriForm!=null && !imageNameET.getText().toString().isEmpty()
            && isImageSelected)
            {
                //yourName.jpeg
                String imageName=imageNameET.getText().toString()+"."+getExtension(imageDataInUriForm);

                //FirebaseStorage/BSCSAImagesFolder/yourName.jpeg
                StorageReference actualImageRef=objectStorageReference.child(imageName);

                UploadTask uploadTask=actualImageRef.putFile(imageDataInUriForm);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return null;
                    }
                });
            }
            else if(imageDataInUriForm==null)
            {
                Toast.makeText(this, "No image is selected", Toast.LENGTH_SHORT).show();
            }
            else if(imageNameET.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please first you need to put image name", Toast.LENGTH_SHORT).show();
                imageNameET.requestFocus();
            }
            else if(!isImageSelected)
            {
                Toast.makeText(this, "Please image view to select image", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "uploadOurImage:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri imageDataInUriForm)
    {
        try
        {
            ContentResolver objectContentResolver=getContentResolver();
            MimeTypeMap objectMimeTypeMap=MimeTypeMap.getSingleton();

            String extension=objectMimeTypeMap.getExtensionFromMimeType(objectContentResolver.getType(imageDataInUriForm));
            return extension;
        }
        catch (Exception e)
        {
            Toast.makeText(this, "getExtension:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return "";
    }
}
