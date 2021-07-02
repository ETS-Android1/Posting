package com.example.post;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.post.aws.S3Uploader;
import com.example.post.aws.S3Utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MakePost extends AppCompatActivity {
    S3Uploader s3uploaderObj;
    String urlFromS3 = null;
    private String TAG = MakePost.class.getCanonicalName();
    Uri UriList[] = new Uri[10];

    RecyclerView recyclerView;
    ListAdapter adapter;
    ItemTouchHelper helper;
    ProgressDialog progressDialog;
    private Toolbar toolbar;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makepost);

        s3uploaderObj = new S3Uploader(MakePost.this);//Uploader 객체
        progressDialog = new ProgressDialog(MakePost.this);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        recyclerView = findViewById(R.id.rv);

        Button btn_getImage = findViewById(R.id.getImage);
        btn_getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    //사진 선택
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//여러장 선택
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
    }

    //선택한 사진 띄우기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter = new ListAdapter();

        if (data.getClipData() == null)
            Toast.makeText(MakePost.this, "No Image", Toast.LENGTH_SHORT).show();

        else {
            ClipData clipData = data.getClipData();
            count = clipData.getItemCount();
            Log.d("카운트", String.valueOf(count));

            if (clipData.getItemCount() > 10)
                Toast.makeText(MakePost.this, "You have up to 10 choices.", Toast.LENGTH_SHORT).show();

            else if (clipData.getItemCount() == 1) {
                Uri imageUri = data.getData();
                SelectedImage selectedImage = new SelectedImage(imageUri);
                adapter.addItem(selectedImage);
            }

            else if (clipData.getItemCount() <= 10 && clipData.getItemCount() > 1) {
                for (int i = 0; i < count; i++) {
                    UriList[i] = clipData.getItemAt(i).getUri();
                    try {
                        SelectedImage selectedImage = new SelectedImage(UriList[i]);
                        adapter.addItem(selectedImage);
                        Log.i("selected img: ", UriList[i].toString());
                    } catch (Exception e) {
                        Log.e(TAG, "File select error", e);
                    }
                }
            }

        }

        recyclerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        helper.attachToRecyclerView(recyclerView);
    }

    //상단 확인버튼 표시
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    //확인 버튼 클릭//
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.complete) {
            for (int j = 0; j < count; j++)
                uploadImageTos3(UriList[j]);
        }

        return super.onOptionsItemSelected(item);
    }

    //업로드 함수
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadImageTos3(Uri UrifromList) {
        final String path = getFilePathFromURI(UrifromList);
        if (path != null) {
            showLoading();
            s3uploaderObj.initUpload(path);
            s3uploaderObj.setOns3UploadDone(new S3Uploader.S3UploadInterface() {
                @Override
                public void onUploadSuccess(String response) {
                    if (response.equalsIgnoreCase("Success")) {
                        hideLoading();
                        urlFromS3 = S3Utils.generates3ShareUrl(getApplicationContext(), path);
                        if (!TextUtils.isEmpty(urlFromS3)) {
                            Toast.makeText(MakePost.this, "Uploaded Successfully!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onUploadError(String response) {
                    hideLoading();
                    Log.e(TAG, "Error Uploading");
                }
            });
        } else {
            Toast.makeText(this, "Null Path", Toast.LENGTH_SHORT).show();
        }
    }

    //업로드 할 사진의 경로 가져오기
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getFilePathFromURI(Uri selectedImageUri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(selectedImageUri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        assert cursor != null;
        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    private void showLoading() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.setMessage("Uploading Image !!");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
