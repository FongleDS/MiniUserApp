package kr.ac.duksung.pongle;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button btn_main;
    EditText stdID; // edittext생성해서 학번
    EditText stdPW; // edittext생성해서 비번
    Button btn_login; // 로그인 button
    JSONArray stdInfo; // 데이터 베이스에서 받아올 학생 정보
    TextView test;
    String ID;
    String PW;
    String realPW;
    boolean loginBool = false;
    //SharedPreferences prefs = getSharedPreferences("stdorderInfo", MODE_PRIVATE);

//나연

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //btn_main = findViewById(R.id.button_main);

        // 학번
        stdID = findViewById(R.id.stdID);
        // 비밀 번호
        stdPW = findViewById(R.id.stdPW);
        // 로그인 버튼
        btn_login = findViewById(R.id.login);

        //login 버튼 작동
        btn_login.setOnClickListener(v -> {
            ID = String.valueOf(stdID.getText());
            PW = String.valueOf(stdPW.getText());

            //SharedPreferences.Editor editor = prefs.edit();
            //editor.putString("stdID", ID);
            //editor.apply();

            fetchPassword(ID);
        });


/*
        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);
            }
        });

 */

    }

    OkHttpClient client = new OkHttpClient();
    public void fetchPassword(String stdID) {
        RequestBody formBody = new FormBody.Builder()
                .add("stdID", stdID)
                .build();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/get_password")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        if (jsonObject.has("password")) {
                            String password = jsonObject.getString("password");
                            runOnUiThread(() -> {
                                System.out.println(password);
                                realPW = password;
                                System.out.println(realPW);
                                System.out.println(PW);

                                // 입력한 비밀번호가 학번의 비밀 번호와 같을 떄
                                if (realPW.equals(PW)) {
                                    Intent intent = new Intent(getApplicationContext(),MainPage.class);
                                    intent.putExtra("stdNum", stdID);
                                    startActivity(intent);
                                }
                                // 입력한 비밀번호가 학번의 비밀 번호와 다를 때
                                else {
                                    stdPW.setText(null);
                                }
                            });
                        } else if (jsonObject.has("error")) {
                            String error = jsonObject.getString("error");
                            runOnUiThread(() -> {
                                System.out.println("error");
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

