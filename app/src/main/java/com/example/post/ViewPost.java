package com.example.post;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ViewPost extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpost);

        PostListAdapter adapter;
        final InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        //작성자 TextView
        TextView viewwriter = (TextView)findViewById(R.id.ViewWriter);

        //날짜 TextView
        TextView viewdate = (TextView)findViewById(R.id.ViewDate);

        //제목 TextView
        EditText viewtitle = (EditText) findViewById(R.id.Viewtitle);

        //내용 TextViw
        EditText viewcontents = (EditText) findViewById(R.id.Viewcontents);

        //이미지 리스트 ListView
//        ListView viewimg = (ListView)findViewById(R.id.Viewimglist);

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
        String[] imgURL = intent.getStringArrayExtra("ImgURL");
//        Log.d("유알엘", imgURL[0]);
//        Log.d("유알엘", imgURL[1]);
//        Log.d("유알엘", imgURL[2]);


        //받아온 값 Setting
        viewwriter.setText(writer);
        viewdate.setText(date);

        viewtitle.setText(title);

        //EditText 비활성화
        viewtitle.setFocusableInTouchMode(false);
        viewtitle.setFocusable(false);

        viewcontents.setText(contents);

        //EditText 비활성화
        viewcontents.setFocusableInTouchMode(false);
        viewcontents.setFocusable(false);

        //TODO
        /*이미지 어떻게 표현할 것인지*/
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear);
        for(int i = 0; i < imgURL.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            Glide.with(this).load(imgURL[i]).into(imageView);
            linearLayout.addView(imageView);
        }


        //수정 버튼
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //수정 버튼 클릭시 EditText 활성화
                viewtitle.setFocusableInTouchMode(true);
                viewtitle.setFocusable(true);

                viewcontents.setFocusableInTouchMode(true);
                viewcontents.setFocusable(true);

                //커서 위치 지정
                viewcontents.setSelection(1);

                //커서 보이기
                viewcontents.setTextIsSelectable(true);

                //수정버튼 눌렀을 때 수정 확인 버튼 활성화
                okbtn.setEnabled(true);
                okbtn.setVisibility(View.VISIBLE);


                //TODO
                /*수정한 내용 DB저장*/
            }
        });

        //수정 확인 버튼
        okbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String edited_title = viewtitle.getText().toString();
                String edited_contents = viewcontents.getText().toString();

                //수정된 내용
                viewtitle.setText(edited_title);
                viewcontents.setText(edited_contents);

                //버튼 숨기기
                okbtn.setEnabled(false);
                okbtn.setVisibility(View.INVISIBLE);

                //EditText 비활성화
                viewtitle.setFocusableInTouchMode(false);
                viewtitle.setFocusable(false);

                viewcontents.setFocusableInTouchMode(false);
                viewcontents.setFocusable(false);

                //키보드 내리기
                manager.hideSoftInputFromWindow(viewcontents.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


                //TODO
                //수정된 내용 DB저장

            }
        });
    }//onCreate 끝

    //삭제 버튼
    public void OnDeleteHandler(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("삭제하시겠습니까?");

        builder.setPositiveButton(Html.fromHtml("<font color='#000000'>확인</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "확인", Toast.LENGTH_SHORT).show();
                //TODO
                //삭제 구현
            }
        });

        builder.setNegativeButton(Html.fromHtml("<font color='#000000'>취소</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show();
                //취소
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
