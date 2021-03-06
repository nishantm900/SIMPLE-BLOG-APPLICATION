package com.example.nishant.simpleblogapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {
    private Toolbar newPostToolbar;
    private ImageView newPostImage;
    private EditText newPostDesc;
    private Button newPostBtn;

    private Uri postImageUri = null;
    private ProgressBar newPostProgress;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;

    private Bitmap compressedImageFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();

        newPostToolbar = (Toolbar) findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle(" ADD NEW POST");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPostImage =(ImageView)findViewById(R.id.new_post_image);
        newPostDesc =(EditText)findViewById(R.id.new_post_desc);
        newPostBtn =(Button)findViewById(R.id.post_btn);
        newPostProgress =(ProgressBar)findViewById(R.id.new_post_progress);


        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        . setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        . start(NewPostActivity.this);

            }
        });

         newPostBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 final String desc = newPostDesc.getText().toString();
                 if (!TextUtils.isEmpty(desc)&& postImageUri != null){
                     newPostProgress.setVisibility(View.VISIBLE);

                     final String randomName = UUID.randomUUID().toString();

                     final StorageReference filePath = storageReference.child("POST_IMAGES").child(randomName + ".JPG");
                     filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                             final String downloadUri = task.getResult().getDownloadUrl().toString();

                             if (task.isSuccessful()){

                                 File newThumbFile = new File(postImageUri.getPath());
                                 try {
                                     compressedImageFile = new Compressor(NewPostActivity.this)
                                             .setMaxHeight(100)
                                             .setMaxWidth(100)
                                             .setQuality(2)
                                             .compressToBitmap(newThumbFile);
                                 } catch (IOException e) {
                                     e.printStackTrace();
                                 }
                                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                 compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                 byte[] thumbData = baos.toByteArray();

                                 UploadTask uploadTask = storageReference.child("POST_IMAGES/THUMBS").child(randomName + ".JPG").putBytes(thumbData);
                                 uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                     @Override
                                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                         String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();

                                         Map<String, Object> postMap = new HashMap<>();
                                         postMap.put("IMAGE_URL", downloadUri);
                                         postMap.put("IMAGE_THUMB",downloadthumbUri);
                                         postMap.put("DESC", desc);
                                         postMap.put("USER_ID", current_user_id);
                                         postMap.put("TIMESTAMP", FieldValue.serverTimestamp());

                                         firebaseFirestore.collection("POSTS").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                             @Override
                                             public void onComplete(@NonNull Task<DocumentReference> task) {
                                                 if (task.isSuccessful()){
                                                     Toast.makeText(NewPostActivity.this,"POST WAS ADDED",Toast.LENGTH_LONG).show();
                                                     Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                                     startActivity(mainIntent);
                                                     finish();

                                                 } else {

                                                 }
                                                 newPostProgress.setVisibility(View.INVISIBLE);



                                             }
                                         });

                                     }
                                 }).addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception e) {
                                           // ERROR HANDLING
                                     }
                                 });



                             } else {

                                 newPostProgress.setVisibility(View.INVISIBLE);

                                 }


                         }
                     });

                 }
             }
         });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

}



