package com.example.signup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddPost extends AppCompatActivity {
    //Permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //Image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    //Image picked will be same in this uri
    Uri image_uri = null;
    //Permission arrays
    String[] cameraPermissions;
    String[] storagePermissions;
    //Views
    EditText book_name, author_name, publisher_name, Description;
    ImageView book_image;
    Button upload;
    //Progress bar
    ProgressDialog pd;
    //Firebase
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference node = db.getReference("Posts");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //Init permission arrays
        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);
        //Init views
        book_name = findViewById(R.id.book_name);
        author_name = findViewById(R.id.author_name);
        publisher_name = findViewById(R.id.publisher_name);
        Description = findViewById(R.id.description);
        upload = findViewById(R.id.upload);
        book_image = findViewById(R.id.bookimage);

        //Get image from camera/gallery onClick
        book_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show image pick dialog
                ShowImagePickDialog();
            }
        });

        //Upload button click listener
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data(Book_name, Author_name, Publisher_name, Description) from edit_texts.
                String _bookname = book_name.getText().toString().trim();
                String _authorname = author_name.getText().toString().trim();
                String _publishername = publisher_name.getText().toString().trim();
                String _description = Description.getText().toString().trim();
                if(TextUtils.isEmpty(_bookname)) {
                    Toast.makeText(AddPost.this, "Enter Book name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(_authorname)) {
                    Toast.makeText(AddPost.this, "Enter Author name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(_publishername)) {
                    Toast.makeText(AddPost.this, "Enter Publisher name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(_description)) {
                    Toast.makeText(AddPost.this, "Enter Description",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(image_uri==null) {
                    //Post without image
                    uploadData(_bookname, _authorname, _publishername, _description,"noImage");
                }
                else {
                    //Post with image
                    uploadData(_bookname, _authorname, _publishername, _description,String.valueOf(image_uri));
                }

            }
        });
    }

    private void uploadData(String bookname, String authorname, String publishername, String description, String uri) {
        pd.setMessage("Publishing Post...");
        pd.show();

        //for post-image, bookname, authorname, publishername, description, post publish time
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/" + "post_" + timeStamp;
        if(!uri.equals("noImage")) {
            //Post with Image
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Image is uploaded to firebase storage, now get its url
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String downloadUri = uriTask.getResult().toString();
                            if(uriTask.isSuccessful()) {
                                //Uri is successful, upload image to firebase database
                                HashMap<Object, String> hashMap = new HashMap<>();
                                //put post info
                                hashMap.put("pId", timeStamp);
                                hashMap.put("Post", downloadUri);
                                hashMap.put("Bookname",bookname);
                                hashMap.put("Authorname",authorname);
                                hashMap.put("Publishername",publishername);
                                hashMap.put("Description",description);
                                hashMap.put("pTime",timeStamp);

                                //path to store post data
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

                                //put data in this reference
                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Added in database
                                                pd.dismiss();
                                                Toast.makeText(AddPost.this, "Post published", Toast.LENGTH_SHORT).show();
                                                //Reset views
                                                book_name.setText("");
                                                author_name.setText("");
                                                publisher_name.setText("");
                                                Description.setText("");
                                                book_image.setImageURI(null);
                                                image_uri = null;

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Failed adding post in database
                                                pd.dismiss();
                                                Toast.makeText(AddPost.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed uploading image
                            pd.dismiss();
                        }
                    });
        }
        else {
            //Post without Image
            HashMap<Object, String> hashMap = new HashMap<>();
            //put post info
            hashMap.put("pId", timeStamp);
            hashMap.put("Post", "noImage");
            hashMap.put("Bookname",bookname);
            hashMap.put("Authorname",authorname);
            hashMap.put("Publishername",publishername);
            hashMap.put("Description",description);
            hashMap.put("pTime",timeStamp);

            //path to store post data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

            //put data in this reference
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Added in database
                            pd.dismiss();
                            Toast.makeText(AddPost.this, "Post published", Toast.LENGTH_SHORT).show();
                            //Reset views
                            book_name.setText("");
                            author_name.setText("");
                            publisher_name.setText("");
                            Description.setText("");
                            book_image.setImageURI(null);
                            image_uri = null;

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed adding post in database
                            pd.dismiss();
                            Toast.makeText(AddPost.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void ShowImagePickDialog() {
        //Options (Camera, Gallery) to show in dialog
        String[] options = { "Camera" , "Gallery"};
        //Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image from");
        //Set options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Item click handle
                if(which == 0) {
                    //Camera clicked
                    if(!checkCameraPermission()) {
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                if(which ==1) {
                    //Gallery clicked
                    if(!checkStoragePermission()) {
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        //Create and show dialog
        builder.create().show();
    }

    private void pickFromCamera() {
        //Intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }
    private void pickFromGallery() {
        //Intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission() {
        //Check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission() {
        //Request runtime storage permission
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission() {
        //Check if camera permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission() {
        //Request runtime camera permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    //Handle permission results

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //This method is called when the user presses allow or deny from permission request dialog
        //Here we will handle permission cases(allowed & denied)
        switch(requestCode) {
            case CAMERA_REQUEST_CODE: {
                if(grantResults.length>0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted) {
                        //both permissions are granted
                        pickFromCamera();
                    }
                    else {
                        //Camera or gallery or both permissions were denied
                        Toast.makeText(this,"Camera & Storage both permissions are necessary", Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                }
                break;
            }
            case STORAGE_REQUEST_CODE: {
                if(grantResults.length>0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted) {
                        //Storage permissions are granted
                        pickFromGallery();
                    }
                    else {
                        //Storage permissions were denied
                        Toast.makeText(this, "Storage permissions are necessary",Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                }
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //This method will be called after picking image from camera or gallery
        if(resultCode==RESULT_OK) {
            if(requestCode==IMAGE_PICK_GALLERY_CODE) {
                //Image is picked from gallery, get uri of image
                image_uri = data.getData();
                //set to imageview
                book_image.setImageURI(image_uri);
            }
            else if(requestCode==IMAGE_PICK_CAMERA_CODE) {
                //Image is picked from camera, get uri of image
//                image_uri = data.getData();
                book_image.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}