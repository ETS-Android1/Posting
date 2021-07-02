//package com.example.post;
//
//import android.app.ProgressDialog;
//import android.content.ClipData;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.DocumentsContract;
//import android.provider.MediaStore;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.ItemTouchHelper;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.bumptech.glide.Glide;
//import com.example.post.aws.S3Uploader;
//import com.example.post.aws.S3Utils;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//
//public class ImgUploader extends AppCompatActivity {
//    Button bt_upload, bt_select, bt_download;
//    ImageView image, download_image;
//    S3Uploader s3uploaderObj;
//    String urlFromS3 = null;
//    Uri imageUri;
//    Uri UriList[] = new Uri[10];
//    private String TAG = ImgUploader.class.getCanonicalName();
//    private int SELECT_PICTURE = 1;
//    private final int GET_GALLERY_IMAGE = 200;
//    ProgressDialog progressDialog;
//    int count = 0;
//
//    private ListView listView;
//    private PostListAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        s3uploaderObj = new S3Uploader(ImgUploader.this);//Uploader 객체
//        progressDialog = new ProgressDialog(ImgUploader.this);
//
//        //이미지 업로드 함수 호출
//        bt_upload.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            public void onClick(View v) {
//                if (count > 0) {
//                    for (int j = 0; j < count; j++)
//                        uploadImageTos3(UriList[j]);
//                } else {
//                    Toast.makeText(ImgUploader.this, "Choose image first", Toast.LENGTH_SHORT).show();//이미지 선택 안 했을 때
//                }
//
//            }
//        });
//
//        //이미지 다운로드 함수 호출
//        bt_download.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DownloadImage();
//            }
//        });
//    }//여기까지 onCreate
//
//    /////////*사진선택 및 ImageView Load과정*/////////
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            chooseImage();
//            Log.e(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
//        } else {
//            Log.e(TAG, "Please click again and select allow to choose profile picture");
//        }
//    }
//
//    //사진 선택
//    private void chooseImage() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, 2222);
//    }
//
//    //선택한 사진 OnPictureSelect함수로 넘기기
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == GET_GALLERY_IMAGE) {
//            if (resultCode == RESULT_OK) {
//                if (data.getClipData() == null) {
//                    Toast.makeText(ImgUploader.this, "No Image", Toast.LENGTH_SHORT).show();
//                } else {
//                    ClipData clipData = data.getClipData();
//                    count = clipData.getItemCount();
//                    Log.d("카운트", String.valueOf(count));
//
//                    if (clipData.getItemCount() > 10)
//                        Toast.makeText(ImgUploader.this, "You have up to 10 choices.", Toast.LENGTH_SHORT).show();
//
//                    else if (clipData.getItemCount() == 1) {
//                        Uri imageUri = data.getData();
//                        SelectedImage selectedImage = new SelectedImage(imageUri);
//                        adapter.addItem(selectedImage);
//                    } else if (clipData.getItemCount() <= 10 && clipData.getItemCount() > 1)
//                        for (int i = 0; i < count; i++) {
//                            Uri imageUri = clipData.getItemAt(i).getUri();  //
//                            UriList.add(imageUri);
//                            try {
//                                SelectedImage selectedImage = new SelectedImage(imageUri);
//                                adapter.addItem(selectedImage);
//                                Log.i("selected img: ", imageUri.toString());
//                            } catch (Exception e) {
//                                Log.e(TAG, "File select error", e);
//                            }
//                        }
//
//                }
//            }
//        }
//        recyclerView.setAdapter(adapter);
//
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        manager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(manager);
//
//        helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
//        helper.attachToRecyclerView(recyclerView);
//    }
//
//    //넘겨받은 사진 ImageView에 띄우기
//    public void OnPictureSelect(Uri data) {
//        imageUri = data;
//        InputStream imageStream = null;
//        try {
//            imageStream = getContentResolver().openInputStream(imageUri);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        if (imageStream != null) {
//            image.setImageBitmap(BitmapFactory.decodeStream(imageStream));
//        }
//    }
//
//    /////////*사진업로드 과정*/////////
//
//    //업로드 함수
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private void uploadImageTos3(Uri imageUri) {
//        final String path = getFilePathFromURI(imageUri);
//        if (path != null) {
//            showLoading();
//            s3uploaderObj.initUpload(path);
//            s3uploaderObj.setOns3UploadDone(new S3Uploader.S3UploadInterface() {
//                @Override
//                public void onUploadSuccess(String response) {
//                    if (response.equalsIgnoreCase("Success")) {
//                        hideLoading();
//                        urlFromS3 = S3Utils.generates3ShareUrl(getApplicationContext(), path);
//                        if (!TextUtils.isEmpty(urlFromS3)) {
//                            Toast.makeText(ImgUploader.this, "Uploaded Successfully!!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//                @Override
//                public void onUploadError(String response) {
//                    hideLoading();
//                    Log.e(TAG, "Error Uploading");
//
//                }
//            });
//        } else {
//            Toast.makeText(this, "Null Path", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    //업로드 할 사진의 경로 가져오기
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private String getFilePathFromURI(Uri selectedImageUri) {
//        String filePath = "";
//        String wholeID = DocumentsContract.getDocumentId(selectedImageUri);
//
//        // Split at colon, use second item in the array
//        String id = wholeID.split(":")[1];
//
//        String[] column = {MediaStore.Images.Media.DATA};
//
//        // where id is equal to
//        String sel = MediaStore.Images.Media._ID + "=?";
//
//        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                column, sel, new String[]{id}, null);
//
//        assert cursor != null;
//        int columnIndex = cursor.getColumnIndex(column[0]);
//
//        if (cursor.moveToFirst()) {
//            filePath = cursor.getString(columnIndex);
//        }
//        cursor.close();
//        return filePath;
//
//    }
//
//    private void showLoading() {
//        if (progressDialog != null && !progressDialog.isShowing()) {
//            progressDialog.setMessage("Uploading Image !!");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }
//    }
//
//    private void hideLoading() {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//    }
//
//    /////////*사진다운로드 과정*/////////
//
//    private void DownloadImage() {
//        String imageUrl = "https://sikigobucket.s3.ap-northeast-2.amazonaws.com/s3Example/" + "your file name";
//        Glide.with(this).load(imageUrl).into(download_image);
//    }
//}
