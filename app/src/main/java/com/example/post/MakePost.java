package com.example.post;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.model.UriLoader;
import com.example.post.aws.S3Uploader;
import com.example.post.aws.S3Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MakePost extends AppCompatActivity {
    S3Uploader s3uploaderObj;
    String urlFromS3 = null;
    private String TAG = MakePost.class.getCanonicalName();
    Uri UriList[] = new Uri[10];
    Uri FinalUriList[] = new Uri[10];
    RecyclerView recyclerView;
    ListAdapter adapter = new ListAdapter();
    ItemTouchHelper helper;
    ProgressDialog progressDialog;
    private Toolbar toolbar;
    int count = 0;

    EditText edit_title;
    EditText edit_article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makepost);

        s3uploaderObj = new S3Uploader(MakePost.this);//Uploader 객체
        progressDialog = new ProgressDialog(MakePost.this);

        edit_title = (EditText) findViewById(R.id.maketitle);
        edit_article = (EditText) findViewById(R.id.makecontents);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        recyclerView = findViewById(R.id.rv);

        recyclerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        helper.attachToRecyclerView(recyclerView);

        //사진 가져오기 버튼
        ImageButton btn_getImage = findViewById(R.id.getImage);
        btn_getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    //사진 선택
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2222);
    }

    //선택한 사진 띄우기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (data.getClipData() == null)
                Toast.makeText(MakePost.this, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();

            else {
                ClipData clipData = data.getClipData();
                count = clipData.getItemCount();

                if (clipData.getItemCount() > 10)
                    Toast.makeText(MakePost.this, "최대 10개의 이미지만 가능합니다.", Toast.LENGTH_SHORT).show();

                else if (clipData.getItemCount() == 1) {
                    UriList[0] = clipData.getItemAt(0).getUri();
                    SelectedImage selectedImage = new SelectedImage(compressImage(UriList[0]));
                    adapter.addItem(selectedImage);
                    adapter.notifyItemInserted(0);
                } else if (clipData.getItemCount() <= 10 && clipData.getItemCount() > 1) {
                    for (int i = 0; i < count; i++) {
                        UriList[i] = compressImage(clipData.getItemAt(i).getUri());
                        try {
                            SelectedImage selectedImage = new SelectedImage(UriList[i]);
                            adapter.addItem(selectedImage);
                            adapter.notifyItemInserted(0);
                            Log.i("selected img: ", UriList[i].toString());
                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }
                }

            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
            return;
        }
    }

    public Uri compressImage(Uri uri) {
        String filename = uri.getLastPathSegment();
        Log.d("파일이름", filename);
        Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        int width = 300; // 축소시킬 너비
        int height = 300; // 축소시킬 높이
        float bmpWidth = bitmap.getWidth();
        float bmpHeight = bitmap.getHeight();

        if (bmpWidth > width) {
            // 원하는 너비보다 클 경우의 설정
            float mWidth = bmpWidth / 100;
            float scale = width/ mWidth;
            bmpWidth *= (scale / 100);
            bmpHeight *= (scale / 100);
        } else if (bmpHeight > height) {
            // 원하는 높이보다 클 경우의 설정
            float mHeight = bmpHeight / 100;
            float scale = height/ mHeight;
            bmpWidth *= (scale / 100);
            bmpHeight *= (scale / 100);
        }

        Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmap, (int) bmpWidth, (int) bmpHeight, true);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), resizedBmp, filename, null);
        return Uri.parse(path);
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
        //TODO
        //제목, 내용 DB저장
        edit_title.setError(null);
        edit_article.setError(null);

        int size = adapter.getItemCount();
        ArrayList<SelectedImage> img = adapter.images;
        for(int i = 0; i < size; i++) {
            FinalUriList[i] = img.get(i).getImage();
        }

        boolean cancel = false;
        View focusView = null;

        String title = edit_title.getText().toString();
        String article = edit_article.getText().toString();


        if (title.isEmpty()) {
            edit_title.setError("제목을 입력하세요.");
            focusView = edit_title;
            cancel = true;
        }

        if (article.isEmpty()) {
            edit_article.setError("내용을 입력하세요.");
            focusView = edit_article;
            cancel = true;
        }else if(article.length() > 200){
            edit_article.setError("200자 까지만 가능합니다.");
            focusView = edit_article;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (item.getItemId() == R.id.complete) {
                //현재 날짜, 시각
                long now = System.currentTimeMillis();
                Date mdate = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                String date = sdf.format(mdate);
                //이미지들의 주소를 하나의 문자열에 저장
                StringBuffer fullImagePath = new StringBuffer("");

                for (int j = 0; j < size; j++) {
                    String imgPath = getFilePathFromURI(FinalUriList[j]);
                    uploadImageTos3(imgPath);
                    fullImagePath.append(imgPath.substring(imgPath.lastIndexOf("/") + 1));
                    fullImagePath.append("&");
                }


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                Log.i("her: ", "here");
                                Toast.makeText(getApplicationContext(), "정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                Log.i("Length:", String.valueOf(count));
                                if(UriList.length > 0) {
                                    for (int i = 0; i < count; i++) {
                                        Log.i("delete", String.valueOf(UriList[i]));
                                        getContentResolver().delete(UriList[i], null, null);
                                    }
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                if(fullImagePath.length() > 1){
                PostRequest postRequest = new PostRequest(title, article, date, fullImagePath.substring(0, fullImagePath.length() - 1), responseListener);
                RequestQueue queue = Volley.newRequestQueue(MakePost.this);
                queue.add(postRequest);
                }else{
                    PostRequest postRequest = new PostRequest(title, article, date, null, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(MakePost.this);
                    queue.add(postRequest);
                }
                finish();
            }
        }

        return super.onOptionsItemSelected(item);

    }

    //업로드 함수
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadImageTos3(String path) {
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
//                            Toast.makeText(MakePost.this, "Uploaded Successfully!!", Toast.LENGTH_SHORT).show();
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
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(this, selectedImageUri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return  result;
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

    public void BackBtn(View view){
        if(UriList.length > 0) {
            for (int i = 0; i < count; i++) {
                Log.i("delete", String.valueOf(UriList[i]));
                getContentResolver().delete(UriList[i], null, null);
            }
        }
        finish();
    }
}