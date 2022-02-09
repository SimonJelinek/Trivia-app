package com.example.trivia;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import java.util.LinkedList;
import java.util.List;

public class Repository {

    private List<Question> questionList = new LinkedList<Question>();
    private RequestQueue queue;

    public void getQuestions(Context context, String url, Requests requests) {

        queue = Volley.newRequestQueue(context);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONArray array = response.getJSONArray(i);
                        questionList.add(new Question(array.getString(0), array.getBoolean(1)));
                    }catch (Exception e) {}
                }
                requests.requestDone(questionList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Main", "Error!");
            }
        });
        queue.add(request);
    }

    public List<Question> getQuestionList() {
        return questionList;
    }
}
