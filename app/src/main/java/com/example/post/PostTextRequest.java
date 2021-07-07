package com.example.post;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PostTextRequest extends StringRequest {

    final static private String URL = "http://whdvm1.dothome.co.kr/Post/UploadText.php";
    private Map<String, String> map;

    public PostTextRequest(String title, String article, String date, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("title", title);
        map.put("article", article);
        map.put("date", date);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
