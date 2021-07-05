package com.example.post;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PostListAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<PostListItem> listItems = new ArrayList<PostListItem>();

    public PostListAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public int getCount() {
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

        // item.xml 레이아웃을 inflate해서 참조획득
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.post_item, parent, false);
        }


        // post_list_item.xml 의 참조 획득
        TextView txt_writer = (TextView)convertView.findViewById(R.id.Writer);
        TextView txt_date = (TextView)convertView.findViewById(R.id.Date);
        TextView txt_title = (TextView)convertView.findViewById(R.id.Title);
        TextView txt_contents = (TextView)convertView.findViewById(R.id.Content);
        ImageView img_thumbnail = (ImageView) convertView.findViewById(R.id.Thumbnail);
        ImageButton btn_delete = (ImageButton)convertView.findViewById(R.id.DeleteBtn);

        PostListItem listItem = listItems.get(position);

        // 가져온 데이터를 텍스트뷰에 입력
        txt_writer.setText(listItem.getWriter());
        txt_date.setText(listItem.getDate());
        txt_title.setText(listItem.getTitle());
        txt_contents.setText(listItem.getContents());
        img_thumbnail.setImageResource(listItem.getImgResource());

        // 리스트 아이템 삭제
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItems.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public void addItem(String writer, String date, String title, String contents, int imageresource){
        PostListItem listItem = new PostListItem();

        listItem.setWriter(writer);
        listItem.setDate(date);
        listItem.setTitle(title);
        listItem.setContents(contents);
        listItem.setImgResource(imageresource);

        listItems.add(listItem);
    }
}
