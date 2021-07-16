package com.example.post;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PostListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<PostListItem> listItems = new ArrayList<PostListItem>();

    /*한 번에 보여줄 게시물*/
    int size = 3;

    public PostListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return size;
    }

    /*현재 리스트 크기*/
    public void setCount(int size) {
        this.size = size;
    }

    /*전체 크기*/
    public int getSize() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        notifyDataSetChanged();

        // item.xml 레이아웃을 inflate해서 참조획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.post_item, parent, false);
        }


        // post_list_item.xml 의 참조 획득
        TextView txt_writer = (TextView) convertView.findViewById(R.id.Writer);
        TextView txt_date = (TextView) convertView.findViewById(R.id.Date);
        TextView txt_title = (TextView) convertView.findViewById(R.id.Title);
        TextView txt_contents = (TextView) convertView.findViewById(R.id.Content);
        ImageView img_thumbnail = (ImageView) convertView.findViewById(R.id.Thumbnail);
        ImageButton btn_delete = (ImageButton) convertView.findViewById(R.id.DeleteBtn);

        PostListItem listItem = listItems.get(position);

        //가져온 데이터를 텍스트뷰에 입력
        txt_writer.setText(listItem.getWriter());
        txt_date.setText(listItem.getDate());
        txt_title.setText(listItem.getTitle());
        txt_contents.setText(listItem.getContents());
        Glide.with(mContext).load(listItem.getImgResource()[0]).into(img_thumbnail);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItems.remove(position);
                notifyDataSetChanged();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("삭제하시겠습니까?");

                try {
                    builder.setPositiveButton(Html.fromHtml("<font color='#000000'>확인</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(mContext, "확인", Toast.LENGTH_SHORT).show();
                            listItems.remove(position);
                            notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton(Html.fromHtml("<font color='#000000'>취소</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(mContext, "취소", Toast.LENGTH_SHORT).show();
                            //취소
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    Toast.makeText(mContext, "더 이상 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    public void addItem(String writer, String date, String title, String contents, String[] imgname) {
        PostListItem listItem = new PostListItem();

        listItem.setWriter(writer);
        listItem.setDate(date);
        listItem.setTitle(title);
        listItem.setContents(contents);
        listItem.setImgResource(imgname);

        listItems.add(listItem);
    }
}
