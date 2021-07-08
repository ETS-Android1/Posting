package com.example.post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isStoragePermissionGranted();//저장소 권한

        final ListView listView;
        PostListAdapter adapter;
        Button PlusBtn = (Button) findViewById(R.id.PlusBtn);


        /*리스트뷰*/

        // 날짜를 문자 배열로 초기화
        String[] dateList = {"2021-06-01", "2021-06-04", "2021-06-07", "2021-06-09", "2021-06-20"};

        // 제목 문자 배열로 초기화
        String[] titleList = {"제목A", "제목B", "제목C", "제목D", "제목E"};

        String contents = "In the culture of simulation, experiences on the Internet figure prominently. In cyberspace, we can talk, exchange ideas, and assume personae of our own creation. In an interactive computer game inspired by Star Trek, thousands of players spend up to eighty hours a week participating in intergalactic exploration and wars. They create characters who have romantic encounters, hold jobs and collect paychecks, attend rituals and celebrations, fall in love and get married. “This is more real than my real life,” says a character who turns out to be a man playing a woman. In this game the self is constructed and the rules of social interaction are built, not received. In another text-based game, each of nearly ten thousand players creates a character or several characters, specifying their genders and other physical and psychological attributes. The characters need not be human and there are more than two genders. Players are invited to help build the computer world itself. Indeed, the Internet links millions of people in new spaces that are changing the way of our thinking, the nature of our sexuality, the form of our communities, and our very identities.";

        // 썸네일 파일을 배열로 초기화
        Integer[] thumbID = {R.drawable.thumbnail1, R.drawable.thumbnail2, R.drawable.thumbnail3, R.drawable.thumbnail4, R.drawable.thumbnail5};

        String staticURL = "https://sikigobucket.s3.ap-northeast-2.amazonaws.com/PostImg/";
        String[][] img = {{staticURL + "57399.jpg", staticURL + "57401.jpg", staticURL + "57403.jpg"}, {staticURL + "57409.jpg"}, {staticURL + "57411.jpg"},
                {staticURL + "57413.jpg"}, {staticURL + "57415.jpg"}};

        //Custom ListView Adapter연결
        listView = (ListView) findViewById(R.id.PostListView);
        adapter = new PostListAdapter(MainActivity.this);
        listView.setAdapter(adapter);

        //Custom ListView Item 추가
        for (int i = 0; i < 5; i++) {
            adapter.addItem("관리자", dateList[i], titleList[i], contents, img[i]);
            adapter.notifyDataSetChanged();
        }

        //게시물 작성 플로팅 버튼
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MakePost.class);
            startActivity(intent);
            overridePendingTransition(R.anim.horizon_enter, R.anim.horizon_exit);
        });

        //게시물 자세히 보기로 넘어가기
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewPost.class);
                String writer = ((PostListItem) adapter.getItem(position)).getWriter();
                String title = ((PostListItem) adapter.getItem(position)).getTitle();
                String date = ((PostListItem) adapter.getItem(position)).getDate();
                String contents = ((PostListItem) adapter.getItem(position)).getContents();
                String[] imgURL = ((PostListItem) adapter.getItem(position)).getImgResource();
//                Log.d("유알엘", imgURL[0]);
//                Log.d("유알엘", imgURL[1]);
//                Log.d("유알엘", imgURL[2]);

                //ViewPost.class로 넘겨주는 값
                intent.putExtra("Writer", writer);//작성자
                intent.putExtra("Title", title);//제목
                intent.putExtra("Contents", contents);//내용
                intent.putExtra("Date", date);//날짜
                intent.putExtra("ImgURL", imgURL);//이미지 값

                startActivity(intent);
                overridePendingTransition(R.anim.horizon_enter, R.anim.horizon_exit);
                adapter.notifyDataSetChanged();
            }
        });

        /*더보기 버튼*/
        PlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int originSize = adapter.getSize();//원래 사이즈
                int editSize = adapter.getCount();//현재 사이즈

                if (editSize == originSize) {
                    Toast.makeText(MainActivity.this, "마지막 게시물입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    editSize += 2;

                    if (editSize <= originSize) {
                        adapter.setCount(editSize);
                        adapter.notifyDataSetChanged();
                    } else if (editSize > originSize) {
                        if (originSize % 2 == 1) {
                            editSize -= 1;
                            if (editSize > originSize) {
                                Toast.makeText(MainActivity.this, "마지막 게시물입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                adapter.setCount(editSize);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "마지막 게시물입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }//여기까지 onCreate

    //저장소 권한얻기
    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 29) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            Log.v(TAG, "Permission is granted");
            Toast.makeText(MainActivity.this, "Permission Success", Toast.LENGTH_SHORT).show();
        }
    }
}
