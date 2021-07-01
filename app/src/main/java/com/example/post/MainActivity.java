package com.example.post;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.post.aws.S3Uploader;
import com.example.post.aws.S3Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getCanonicalName();
    private ListView listView;
    private PostListAdapter adapter;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isStoragePermissionGranted();//저장소 권한


        /*밑줄긋기*/
        textView = (TextView)findViewById(R.id.BandNews);
        String NewsString = "라이온스클럽 소식";
        SpannableString content = new SpannableString(NewsString);
        content.setSpan(new UnderlineSpan(), 0, NewsString.length(), 0);
        textView.setText(content);


        /*리스트뷰*/

        // 작성자를 문자 배열로 초기화
        String[] writerName = {"김고은", "송중기", "김태희", "관리자", "관리자"};

        // 날짜를 문자 배열로 초기화
        String[] dateList = {"2021-06-01", "2021-06-04", "2021-06-07", "2021-06-09", "2021-06-20"};

        // 제목 문자 배열로 초기화
        String[] titleList = {"제목A", "제목B", "제목C", "제목D", "제목E"};

        // 썸네일 파일을 배열로 초기화
        Integer[] thumbID = {R.drawable.thumbnail1, R.drawable.thumbnail2, R.drawable.thumbnail3, R.drawable.thumbnail4, R.drawable.thumbnail5};

        listView = (ListView)findViewById(R.id.PostListView);

        adapter = new PostListAdapter(MainActivity.this);
        listView.setAdapter(adapter);

        for(int i = 0; i < 5; i++){
            adapter.addItem(writerName[i], dateList[i], titleList[i], thumbID[i]);
            adapter.notifyDataSetChanged();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MakePost.class);
            startActivity(intent);
            overridePendingTransition(R.anim.horizon_enter, R.anim.horizon_exit);
        });
    }//여기까지 onCreate

    //저장소 권한얻기
    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 29) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission Success", Toast.LENGTH_SHORT).show();
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            Log.v(TAG, "Permission is granted");
            Toast.makeText(MainActivity.this, "Permission Success", Toast.LENGTH_SHORT).show();
        }
    }


}
