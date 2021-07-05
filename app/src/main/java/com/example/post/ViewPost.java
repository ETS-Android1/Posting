package com.example.post;

import android.content.Intent;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ViewPost extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpost);

        PostListAdapter adapter;

        //작성자 TextView
        TextView viewwriter = (TextView)findViewById(R.id.ViewWriter);
        //날짜 TextView
        TextView viewdate = (TextView)findViewById(R.id.ViewDate);
        //제목 TextView
        EditText viewtitle = (EditText) findViewById(R.id.Viewtitle);
        //내용 TextViw
        EditText viewcontents = (EditText) findViewById(R.id.Viewcontents);
        //이미지 리스트 ListView
        ListView viewimg = (ListView)findViewById(R.id.Viewimglist);
        //수정 버튼
        ImageButton editbtn = (ImageButton)findViewById(R.id.ViewEditBtn);
        //삭제 버튼
        ImageButton delbtn = (ImageButton)findViewById(R.id.ViewDeleteBtn);
        //수정 확인 버튼
        ImageButton okbtn = (ImageButton)findViewById(R.id.ViewOkBtn);

        //MainActivity에서 받아오는 값
        Intent intent = getIntent();
        String writer = intent.getStringExtra("Writer");
        String date = intent.getStringExtra("Date");
        String title = intent.getStringExtra("Title");
        String contents = intent.getStringExtra("Contents");
        int imgres = intent.getIntExtra("Imgres", 0);


        //받아온 값 Setting
        viewwriter.setText(writer);
        viewdate.setText(date);

        viewtitle.setText(title);
        viewtitle.setFocusableInTouchMode(false);
        viewtitle.setFocusable(false);

        viewcontents.setText(contents);
        viewcontents.setFocusableInTouchMode(false);
        viewcontents.setFocusable(false);

        //TODO
        /*이미지 어떻게 표현할 것인지*/

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //수정 버튼 클릭시 EditText 활성화
                viewtitle.setFocusableInTouchMode(true);
                viewtitle.setFocusable(true);

                viewcontents.setFocusableInTouchMode(true);
                viewcontents.setFocusable(true);

                //수정버튼 눌렀을 때 수정 확인 버튼 활성화
                okbtn.setEnabled(true);
                okbtn.setVisibility(View.VISIBLE);


                //TODO
                /*수정한 내용 DB저장*/
            }
        });
    }


}
