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
import android.widget.AdapterView;
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
    String TAG = MainActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView;
        PostListAdapter adapter;
        TextView textView;

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

        String contents = "In the culture of simulation, experiences on the Internet figure prominently. In cyberspace, we can talk, exchange ideas, and assume personae of our own creation. In an interactive computer game inspired by Star Trek, thousands of players spend up to eighty hours a week participating in intergalactic exploration and wars. They create characters who have romantic encounters, hold jobs and collect paychecks, attend rituals and celebrations, fall in love and get married. “This is more real than my real life,” says a character who turns out to be a man playing a woman. In this game the self is constructed and the rules of social interaction are built, not received. In another text-based game, each of nearly ten thousand players creates a character or several characters, specifying their genders and other physical and psychological attributes. The characters need not be human and there are more than two genders. Players are invited to help build the computer world itself. Indeed, the Internet links millions of people in new spaces that are changing the way of our thinking, the nature of our sexuality, the form of our communities, and our very identities.";

        // 썸네일 파일을 배열로 초기화
        Integer[] thumbID = {R.drawable.thumbnail1, R.drawable.thumbnail2, R.drawable.thumbnail3, R.drawable.thumbnail4, R.drawable.thumbnail5};

        listView = (ListView)findViewById(R.id.PostListView);

        adapter = new PostListAdapter(MainActivity.this);
        listView.setAdapter(adapter);

        for(int i = 0; i < 5; i++){
            adapter.addItem(writerName[i], dateList[i], titleList[i], contents, thumbID[i]);
            adapter.notifyDataSetChanged();
        }

        //게시물 작성 플로팅 버튼
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            isStoragePermissionGranted();//저장소 권한
            Intent intent = new Intent(MainActivity.this, MakePost.class);
            startActivity(intent);
            overridePendingTransition(R.anim.horizon_enter, R.anim.horizon_exit);
        });

        //게시물 자세히 보기로 넘어가기
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewPost.class);
                String writer = ((PostListItem)adapter.getItem(position)).getWriter();
                String title = ((PostListItem)adapter.getItem(position)).getTitle();
                String date = ((PostListItem)adapter.getItem(position)).getDate();
                String contents = ((PostListItem)adapter.getItem(position)).getContents();
                int imgres = ((PostListItem)adapter.getItem(position)).getImgResource();

                intent.putExtra("Writer", writer);
                intent.putExtra("Title", title);
                intent.putExtra("Contents", contents);
                intent.putExtra("Date", date);
                intent.putExtra("Imgres", imgres);

                startActivity(intent);
                overridePendingTransition(R.anim.horizon_enter, R.anim.horizon_exit);
                adapter.notifyDataSetChanged();
            }
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
