package com.example.lululab;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.example.lululab.Adapter.MessageAdapter;
import com.example.lululab.Model.Message;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import android.Manifest;
public class ChatActivity extends AppCompatActivity {
    OkHttpClient client;
    RecyclerView recycler_view;
    TextView tv_welcome;
    EditText et_msg;
    ImageButton btn_send;
    ImageButton btn_camera;
    ImageButton btn_questionlist;
    Button chat_item_button;
    List<Message> messageList;
    MessageAdapter messageAdapter;

    List<CSVRecord> cosmetics;
    Map<String, String> mymap = new HashMap<>();

    List<String> pastQuestions = new ArrayList<>();

    //
    private boolean isCamera;

    private boolean isRelatedQuestionGenerated = false;
    private AlertDialog questionListDialog;
    private int flag;

    private boolean ViewDetailClicked = false;
    //
    private String[] questionList = {
            "피부에 좋은 음식은 뭐에요?",
            "피부에 좋은 생활 습관은 뭐에요?",
            "여드름은 왜 생기는거에요?",
            "두드러기는 왜 생기는거에요?",
            "좁쌀 여드름이 생기는 원인이 뭐에요?",
            "피부가 너무 건조한데 해결방법에는 무엇이 있나요?",
            "모공각화증은 무엇인가요?",
            "좁쌀 여드름은 어떻게 해결하나요?",
            "각질 제거는 어떻게 하는게 좋나요?",
            "여름철에 자주 나타나는 피부 트러블은 무엇이 있을까요?",
            "건조한 피부를 가지고 있는데, 적절한 보습 방법이 있을까요?",
            "여드름을 예방하고 치료하기 위해 어떤 스킨케어 방법을 추천하시나요?",
            "피부 알러지의 주요 증상과 대처법은 무엇인가요?",
            "햇빛에 노출되면서 생기는 피부 손상을 예방하기 위해 해야 할 일은 무엇인가요?",
            "민감성 피부를 가지고 있는데, 피부를 진정시키는 방법은 무엇인가요?",
            "고령화로 인해 생기는 주름을 완화시키기 위해 사용할 수 있는 스킨케어 제품은 무엇인가요?",
            "피부가 민감해서 화장품을 선택할 때 주의해야 할 점은 무엇인가요?",
            "피부에 나타나는 멍과 피부색의 변화는 어떤 원인으로 인해 생기나요?",
            "피부 건강을 유지하기 위해 꾸준히 실천해야 할 스킨케어 습관은 무엇인가요?"

            // Add more questions as needed
    };

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private String MY_SECRET_KEY;

    private String skinType;
    private String lowestScoreCondition;

    private String stdid;
    private int cameraFlag = 0;
    static JSONObject usersData;

    // 카메라 권한 허용
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int PERMISSIONS_REQUEST = 100;

    public ChatActivity() throws JSONException {
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeApp();
        MY_SECRET_KEY = "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";
        usersData = getUsersDataFromAsset("users_data.json");
        flag = 0;




        ////////////////////////////////
        String[] categories = new String[] {"화장품","선케어","선로션","선스틱","선스프레이","선쿠션","선크림","선파우더","스크럽","필링","아이크림","에센스","세럼","앰플","크림","로션","밤","보습크림","수딩젤","수분크림","스팟젤","에멀젼","클렌징","립&아이 리무버","클렌징 밀크","클렌징 밤","클렌징 비누","클렌징 오일","클렌징 워터","클렌징 젤","클렌징 크림","클렌징 티슈","클렌징 파우더","클렌징 패드", "클렌징 폼","토너","스킨","팩","마스크팩","모델링팩","슬리핑팩","시트팩","워시오프팩","코팩","패치","필오프팩"};
        mymap.put("화장품","화장품");
        mymap.put("선케어","[category]==선케어"); mymap.put("선스틱","[detail]==선스틱"); mymap.put("선로션","[detail]==선로션"); mymap.put("선스프레이","[detail]==선스프레이"); mymap.put("선쿠션","[detail]==선쿠션"); mymap.put("선크림","[detail]==선크림"); mymap.put("선파우더","[detail]==선파우더");
        mymap.put("스크럽","[detail]==스크럽"); mymap.put("필링","[detail]==필링");
        mymap.put("아이크림","[detail]==아이크림");
        mymap.put("에센스","[detail]==에센스"); mymap.put("세럼","[detail]==세럼"); mymap.put("앰플","[detail]==앰플");
        mymap.put("크림","[category]==크림"); mymap.put("로션","[detail]==로션"); mymap.put("밤","[detail]==밤"); mymap.put("보습크림","[detail]==보습크림"); mymap.put("스팟젤","[detail]==스팟젤"); mymap.put("에멀젼","[detail]==에멀젼");
        mymap.put("클렌징","[category]==클렌징"); mymap.put("립&아이 리무버","[detail]==립&아이 리무버"); mymap.put("클렌징 밀크","[detail]==클렌징 밀크"); mymap.put("클렌징 밤","[detail]==클렌징 밤"); mymap.put("클렌징 비누","[detail]==클렌징 비누"); mymap.put("클렌징 오일","[detail]==클렌징 오일"); mymap.put("클렌징 워터","[detail]==클렌징 워터"); mymap.put("클렌징 젤","[detail]==클렌징 젤"); mymap.put("클렌징 크림","[detail]==클렌징 크림"); mymap.put("클렌징 티슈","[detail]==클렌징 티슈"); mymap.put("클렌징 파우더","[detail]==클렌징 파우더"); mymap.put("클렌징 패드","[detail]==클렌징 패드"); mymap.put("클렌징 폼","[detail]==클렌징 폼");
        mymap.put("토너","[detail]==토너"); mymap.put("스킨","[detail]==스킨");
        mymap.put("팩","[category]==팩"); mymap.put("마스크팩","[detail]==마스크팩"); mymap.put("모델링팩","[detail]==모델링팩"); mymap.put("슬리핑팩","[detail]==슬리핑팩"); mymap.put("시트팩","[detail]==시트팩"); mymap.put("워시오프팩","[detail]==워시오프팩"); mymap.put("코팩","[detail]==코팩"); mymap.put("패치","[detail]==패치"); mymap.put("필오프팩","[detail]==필오프팩");



        ////////////////////////////







        // 챗봇 답변 시간 조절
        client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        checkPermissions();

        recycler_view = findViewById(R.id.recycler_view);
        tv_welcome = findViewById(R.id.tv_welcome);
        et_msg = findViewById(R.id.et_msg);
        btn_send = findViewById(R.id.btn_send);
        btn_camera = findViewById(R.id.btn_camera);
        btn_questionlist = findViewById(R.id.btn_list);
        View chatItemView = getLayoutInflater().inflate(R.layout.chat_item, null);
        chat_item_button = chatItemView.findViewById(R.id.chat_item_view_button);

        recycler_view.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recycler_view.setLayoutManager(manager);

        stdid = MainActivity.studentId;

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recycler_view.setAdapter(messageAdapter);
        System.out.println("MsgAdapter");

        // send 버튼을 통해 챗봇과 메시지를 주고 받을 수 있음
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = et_msg.getText().toString().trim();
                et_msg.setText("");

                addToChat(question, Message.SENT_BY_ME, null);
                callAPI(question);

                tv_welcome.setVisibility(View.GONE);
            }
        });

        recycler_view.addItemDecoration(new RecyclerViewDecoration(20));

        messageAdapter.setOnButtonClickListener(new MessageAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClick(int position) throws JSONException {
                // position을 사용하여 필요한 동작을 수행
                // persistMessages();
                // goToDictionaryView();
                JSONObject userData = usersData.getJSONObject(stdid);
                String userskintype = userData.getString("skintype");
                List<String> lowestScoreCondition = getLowestScoreCondition(userData);
                addToChat("...", Message.SENT_BY_BOT, null);

                JSONObject object = new JSONObject();
                try {
                    object.put("question", "Recommend 'three' random '"+ "화장품" +"' which contain '" + userskintype + "' skin type and 'must contain' skin concerns 'BOTH' '" + lowestScoreCondition.get(0) + "' 'AND' '" + lowestScoreCondition.get(1) + "'.sample(3). Print it");
                    Log.d("gpt_function", object.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(object.toString(), JSON);
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.recommend_url))
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        addResponse("Failed to load response due to " + e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            JSONObject jsonObject = null;
                            String name = null;
                            try {
                                jsonObject = new JSONObject(response.body().string());
                                String result = jsonObject.getString("result");
                                result = result.replace("삵", "");

                                StringBuilder namesBuilder = new StringBuilder();

                                if(result.contains("|")){
                                    String[] rows = result.split("\n");

                                    for (int i = 2; i < rows.length; i++) { // 첫 번째와 두 번째 줄은 헤더이므로 건너뛰기
                                        String[] columns = rows[i].split("\\|");
                                        name = columns[columns.length -2].trim(); // name은 마지막 열 바로 앞에 있음
                                        namesBuilder.append(i-1 + ". ");
                                        namesBuilder.append(name);
                                        namesBuilder.append("\n"); // 줄바꿈 문자를 추가
                                    }

                                    String allNames = namesBuilder.toString().trim(); // 마지막 줄바꿈 문자를 제거하기 위해 trim 사용
                                    String answer = "사용자에 맞는 화장품을 추천하겠습니다.\n\n" + allNames;
                                    Log.d("parse", answer);
                                    addResponse(answer);
                                }
                                else{
                                    addResponse(result);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 여기서 generateRelatedQuestions 호출
                                        generateRelatedQuestions(userQuestions.get(userQuestions.size() - 1));
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            addResponse("Failed to load response due to " + response.body().string());
                        }
                    }
                });
            }
        });

        // 사용자의 피부 사진을 찍을 수 있는 카메라 기능
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                isCamera = true;
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        btn_questionlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRelatedQuestionGenerated && !relatedQuestions.isEmpty()) {
                    showRelatedQuestionList(relatedQuestions);
                } else {
                    showQuestionList();
                }
            }
        });
    }

    private JSONObject getUsersDataFromAsset(String filename) {
        AssetManager assetManager = getAssets();
        try {
            InputStream is = assetManager.open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            return new JSONObject(sb.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showQuestionList() {
        // 다이얼로그 창을 닫은 후에도 다시 표시할 수 있도록 초기화
        questionListDialog = null;
        showQuestionListDialog();
    }

    private void showQuestionListDialog() {
        // 질문 리스트를 보여주는 다이얼로그 창
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("추천 질문");

        // Randomly select three questions from the question list
        List<String> selectedQuestions = new ArrayList<>();
        Random random = new Random();
        Set<Integer> selectedIndices = new HashSet<>();
        while (selectedIndices.size() < 3 && selectedIndices.size() < questionList.length) {
            int randomIndex = random.nextInt(questionList.length);
            if (!selectedIndices.contains(randomIndex)) {
                selectedIndices.add(randomIndex);
                selectedQuestions.add(questionList[randomIndex]);
            }
        }

        // Set the selected questions as the dialog items
        builder.setItems(selectedQuestions.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                // Get the selected question from the list
                String selectedQuestion = selectedQuestions.get(position);

                userQuestions.add(selectedQuestion);
                // Add the selected question to the chat and send it to the chatbot
                addToChat(selectedQuestion, Message.SENT_BY_ME, null);
                callAPI(selectedQuestion);

                // Print the question to the console
                Log.i("Asked Questions", "Question added: " + selectedQuestion);
                // Print the entire list to the console
                Log.i("Asked Questions", "Current list: " + userQuestions.toString());
            }
        });


        // Add a cancel option to the dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // Close the dialog
                // 'Cancel' 버튼 클릭 시에도 다시 질문 목록 창을 표시
                // showQuestionListDialog();
            }
        });

        // Display the dialog
        questionListDialog = builder.show();
    }

    // 가장 낮은 점수의 피부 점수를 반환
    private static List<String> getLowestScoreCondition(JSONObject userData) {
        if (userData == null) {
            Log.e("error", "User data is null");
            return null;
        }

        String[] conditions = {"모공", "피지", "붉은기", "트러블", "주름", "색소침착", "다크써클", "탄력", "생기"};
        Map<String, Double> conditionScores = new HashMap<>();
        try {
            for (String condition : conditions) {
                if (!userData.has(condition)) {
                    Log.e("error", "Condition " + condition + " not found in user data");
                    return null;
                }
                conditionScores.put(condition, userData.getDouble(condition));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (conditionScores.isEmpty()) {
            Log.e("error", "Condition scores is empty");
            return null;
        }

        return conditionScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // 카메라 권한 체크
    private boolean checkPermissions() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                // 권한이 거부된 경우, 사용자에게 설명을 제공하거나 기능을 비활성화합니다.
                Toast.makeText(this, "Camera permission is required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            View chatItemView = getLayoutInflater().inflate(R.layout.chat_item, null);
            ImageView newImageView = chatItemView.findViewById(R.id.newImageView);

            newImageView.setImageBitmap(bitmap);

            // Bitmap을 파일로 변환합니다.
            String fileName = "upload" + System.currentTimeMillis() + ".jpg";
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String url = file.getAbsolutePath();
            addToChat(null, Message.SENT_BY_ME, url);
            tv_welcome.setVisibility(View.GONE);

            uploadImage(url);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String stdid = MainActivity.studentId;
                    if (usersData.has(stdid)) {
                        try {
                            JSONObject userData = usersData.getJSONObject(stdid);
                            String skinType = userData.getString("skintype");
                            List<String> lowestScoreCondition = getLowestScoreCondition(userData);

                            flag = 1;
                            Log.d("check5", userQuestions.toString());


                            String message = "Skin Type: " + skinType + ", Lowest Score Condition: " + lowestScoreCondition;
                            addToChat(message, Message.SENT_BY_SYSTEM, null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, 1000);
        }
    }

    void addToChat(String message, String sentBy, String imageUrl) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy, imageUrl));
                messageAdapter.notifyDataSetChanged();
                recycler_view.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response) {
        messageList.remove(messageList.size() - 1);
        addToChat(response, Message.SENT_BY_BOT, null);
    }

    private List<String> userQuestions = new ArrayList<>();

    void callAPI(String question) {
        // 사용자의 질문을 리스트에 저장
        userQuestions.add(question);
        tv_welcome.setVisibility(View.GONE);
        Log.d("gpt_function", "callAPI");

        addToChat("...", Message.SENT_BY_BOT, null);
        JSONArray arr = new JSONArray();
        JSONObject baseAi = new JSONObject();

        int count = 0;

        try {
            // AI 속성설정
            baseAi.put("role", "system");
            baseAi.put("content", "You are a helpful and kind AI Assistant.");
            // array로 담아서 한번에 보낸다
            arr.put(baseAi);


            // 저장된 모든 사용자 질문을 API에 전송
            /*
            for (String userQuestion : userQuestions) {
                JSONObject userMsg = new JSONObject();
                userMsg.put("role", "user");
                userMsg.put("content", userQuestion);
                arr.put(userMsg);
            }

             */


            JSONObject userMsg = new JSONObject();
            userMsg.put("role","user");
            userMsg.put("content",userQuestions.get(userQuestions.size() - 1));
            arr.put(userMsg);


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        JSONArray functionsArray = new JSONArray();
        //functionsArray.put(skin_st_metadata);
        functionsArray.put(skin_metadata);
        functionsArray.put(recommend_cos_metadata);
        functionsArray.put(camera_metadata);

        Log.d("gpt_function", "function.add end");
        JSONObject object = new JSONObject();
        try {
            object.put("model", "gpt-3.5-turbo-0613");
            object.put("messages", arr);
            object.put("temperature", 0);
            object.put("max_tokens", 200);
            object.put("functions", functionsArray);
            Log.d("gpt_function", "functionsArray" + functionsArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("gpt_function", "Before RequestBody");
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + MY_SECRET_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to " + e.getMessage());
                Log.d("gpt_function", e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    Log.d("gpt_function", "in onResponse");
                    try {

                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        JSONObject messageObject = jsonArray.getJSONObject(0).getJSONObject("message");
                        Log.d("gpt_function", jsonArray.toString());

                        if (messageObject.has("function_call"))
                        {
                            String function_name = jsonArray.getJSONObject(0).getJSONObject("message").getJSONObject("function_call").getString("name");
                            Log.d("gpt_function", "function_name : " + function_name);
                            Log.d("gpt_function", "kw args : " + jsonArray.getJSONObject(0).getJSONObject("message").getJSONObject("function_call").getString("arguments"));

                            String kw_args_str =   jsonArray.getJSONObject(0).getJSONObject("message").getJSONObject("function_call").getString("arguments");
                            JSONObject kw_args = new JSONObject(kw_args_str);
                            Log.d("gpt_function", "Before switch");
                            String output;
                            switch (function_name) {
                                case "skin":
                                    output = skin(kw_args.getString("st"), kw_args.getString("sc1"), kw_args.getString("sc2"), kw_args.getString("category"));
                                    Log.d("gpt_function", "skin");
                                    break;
                                case "recommend_cos":
                                    output = recommend_cos(kw_args.getString("symbol"));
                                    Log.d("gpt_function", "rec_cos");
                                    break;

                                case "camera_rec":
                                    output = camera_rec(kw_args.getString("cam"));
                                    Log.d("gpt_function","camera_rec");
                                    break;
                                default:
                                    Log.d("gpt_function", "normal question");
                                    throw new IllegalArgumentException("Invalid function name: " + function_name);


                            }
                        }
                        else {
                            String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                            addResponse(result.trim());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 여기서 generateRelatedQuestions 호출
                                generateRelatedQuestions(userQuestions.get(userQuestions.size() - 1));
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    addResponse("Failed to load response due to " + response.body().string());
                }
            }
        });
    }

    void initializeApp() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.cosmetic);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader("lululab_skintypes", "lululab_skin_concerns", "name").parse(reader);

            cosmetics = new ArrayList<>();
            for (CSVRecord record : records) {
                cosmetics.add(record);
                Log.d("try", cosmetics.toString());
            }
        } catch (IOException e) {
            Log.e("try", e.getMessage());
        }
    }

    void uploadImage(String filePath) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(), requestFile)
                .build();

        // Define your server endpoint here
        String serverUrl = "http://121.138.183.119:31002/upload";

        Request request = new Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle failure here
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle success here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChatActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            cameraFlag = 1;
                        }
                    });
                } else {
                    // Handle failure here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChatActivity.this, "Image upload failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        handleIntentExtras();
    }

    public void restoreMessages() {
        // 이전에 저장한 채팅 메시지를 복원하여 chatMessages에 추가
        // 예시: SharedPreferences를 사용한 데이터 복원
        SharedPreferences preferences = getSharedPreferences("chat_data", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("chat_messages", "");
        Type type = new TypeToken<List<Message>>() {
        }.getType();
        List<Message> restoredMessages = gson.fromJson(json, type);

        if (restoredMessages != null) {
            messageList.addAll(restoredMessages);
        }
    }

    public void persistMessages() {
        // chatMessages를 SharedPreferences에 저장
        SharedPreferences preferences = getSharedPreferences("chat_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(messageList);
        editor.putString("chat_messages", json);
        editor.apply();
    }

    private void goToDictionaryView() {
        // 현재의 채팅 내용을 저장
        List<Message> savedChatMessages = new ArrayList<>(messageList);

        // dictionary view로 이동하기 위한 Intent 생성
        Intent intent = new Intent(ChatActivity.this, DictionaryActivity.class);
        intent.putExtra("chat_messages", (Serializable) savedChatMessages);

        // ChatActivity에서 DictionaryActivity로 이동
        startActivity(intent);
    }

    private void handleIntentExtras() {
        Intent intent = getIntent();
        if (isCamera != true && intent != null && intent.hasExtra("restore_messages")) {
            boolean restoreMessages = intent.getBooleanExtra("restore_messages", false);
            if (restoreMessages) {
                System.out.println("Restore Message");
                restoreMessages(); // 메세지 리스트 초기화
            }
        }
        isCamera = false;
    }


    private List<String> relatedQuestions = new ArrayList<>();

    //자동 생성
    private void generateRelatedQuestions(String seedQuestion) {
        et_msg = findViewById(R.id.et_msg);
        et_msg.setEnabled(false);
        btn_questionlist.setEnabled(false);
        JSONObject data = new JSONObject();
        try {
            // AI 속성 설정
            JSONObject baseAi = new JSONObject();
            baseAi.put("role", "system");
            baseAi.put("content", "You are a helpful and kind AI Assistant.");

            // 사용자 메시지
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", seedQuestion + "가 피부, 화장품과 조금이라도 관련된 내용이면" + seedQuestion + "에 질문할 수 있는 다른 질문 3개를 각각 한 줄 씩 의문문으로 작성해줘");

            // 메시지 배열 생성
            JSONArray messages = new JSONArray();
            messages.put(baseAi);
            messages.put(userMsg);

            data.put("model", "gpt-3.5-turbo");
            data.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(data.toString(), JSON);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + MY_SECRET_KEY)
                .post(body)
                .build();
        Log.d("check", "me");
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // API 요청 실패 처리
                Log.d("check3", "me");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("check2", "me");
                    JSONObject jsonObject = null;
                    try {
                        relatedQuestions = new ArrayList<>();
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");

                        String[] questions = result.split("\n");

                        // 각 문자열에서 제일 앞에 있는 숫자와 컴마 제거
                        for (int j = 0; j < questions.length; j++) {
                            String question = questions[j];
                            int index = question.indexOf(".");
                            if (index != -1 && index + 1 < question.length()) {
                                questions[j] = question.substring(index + 1).trim();
                            }
                        }

                        // List에 추가
                        relatedQuestions.addAll(Arrays.asList(questions));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //addResponse(result.trim());
                                et_msg.setEnabled(true);
                                btn_questionlist.setEnabled(true);
                                if (relatedQuestions.size() >= 3) {
                                    isRelatedQuestionGenerated = true;
                                    System.out.println("OK");
                                } else {
                                    System.out.println("SAD");
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    et_msg.setEnabled(true);
                    Log.d("check1", "me");
                    // API 요청 실패 처리
                }
            }
        });
    }

    // 관련 질문 목록을 표시하는 다이얼로그 표시
    private void showRelatedQuestionList(List<String> relatedQuestions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("추천 질문");
        pastQuestions = new ArrayList<>(relatedQuestions);
        final CharSequence[] items = relatedQuestions.toArray(new CharSequence[relatedQuestions.size()]);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedQuestion = relatedQuestions.get(which);
                // Add the selected question to the list
                userQuestions.add(selectedQuestion);
                addToChat(selectedQuestion, Message.SENT_BY_ME, null);
                callAPI(selectedQuestion);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //////////////
    //////////////
    //////////////
    //////////////
    //챗봇 관련 함수

    //required에 들어가는
    String[] skin_st_array = new String[] {"st"};
    String[] camera_array = new String[] {"cam"};
    String[] skin_array = new String[] {"st", "sc1", "sc2", "category"};
    String[] rec_cos_array = new String[] {"symbol"};

    String[] skintype = new String[] {"OS","OR","NR","NS","DR","DS","OR-A","DR-A","NS-A","NR-A","DS-A","OS-A"};
    String[] skinconcern = new String[] {"붉은기","주름","색소침착","트러블","다크서클","피지","탄력","모공"};

    String[] categories = new String[] {"화장품","선케어","선로션","선스틱","선스프레이","선쿠션","선크림","선파우더","스크럽","필링","아이크림","에센스","세럼","앰플","크림","로션","밤","보습크림","수딩젤","수분크림","스팟젤","에멀젼","클렌징","립&아이 리무버","클렌징 밀크","클렌징 밤","클렌징 비누","클렌징 오일"," 클렌징 워터","클렌징 젤","클렌징 크림","클렌징 티슈","클렌징 파우더","클렌징 패드", "클렌징 폼","토너","스킨","팩","마스크팩","모델링팩","슬리핑팩","시트팩","워시오프팩","코팩","패치","필오프팩"};



    //skin_st_metadata
    JSONObject skin_st_metadata = new JSONObject()
            .put("name", "skin_st")
            .put("description", "Recommend cosmetics depending on user`s skin type.")
            .put("parameters", new JSONObject()
                    .put("type", "object")
                    .put("properties", new JSONObject()
                            .put("st", new JSONObject()
                                    .put("type", "string")
                                    .put("description", "skin type")
                                    .put("enum", new JSONArray(skintype))
                            )
                    )
                    .put("required", new JSONArray(skin_st_array))
            );
    JSONObject camera_metadata = new JSONObject()
            .put("name", "camera_rec")
            .put("description", "Works when the user wants skin 'analysis'.  e.g : '내 피부 어때?', '내 얼굴 어때?', '내 얼굴 좀 좋아진 것 같지 않아?'")
            .put("parameters", new JSONObject()
                    .put("type", "object")
                    .put("properties", new JSONObject()
                            .put("cam", new JSONObject()
                                    .put("type", "string")
                                    .put("description", "and word")
                            )
                    )
                    .put("required", new JSONArray(camera_array))
            );

    JSONObject skin_metadata = new JSONObject()
            .put("name", "skin")
            .put("description", "Recommend cosmetics depending on user`s skin type and exactly two skin concerns.  e.g : '붉은기와 트러블에 효과적인 화장품을 추천해줘.'")
            .put("parameters", new JSONObject()
                    .put("type", "object")
                    .put("properties", new JSONObject()
                            .put("st", new JSONObject()
                                    .put("type", "string")
                                    .put("enum", new JSONArray(skintype))
                                    .put("description", "skin type")
                            )
                            .put("sc1", new JSONObject()
                                    .put("type", "string")
                                    .put("enum", new JSONArray(skinconcern))
                                    .put("description", "First skin concern")
                            )
                            .put("sc2", new JSONObject()
                                    .put("type", "string")
                                    .put("enum", new JSONArray(skinconcern))
                                    .put("description", "Second skin concern")
                            )
                            .put("category", new JSONObject()
                                    .put("type", "string")
                                    .put("enum", new JSONArray(categories))
                                    .put("description", "category or detail")

                            )
                    )
                    .put("required", new JSONArray(skin_array))

            );

    JSONObject recommend_cos_metadata = new JSONObject()
            .put("name", "recommend_cos")
            .put("description", "Recommend cosmetics when user wants it.  e.g : '요즘 너무 건조해', '화장품 추천해줘'")
            .put("parameters", new JSONObject()
                    .put("type", "object")
                    .put("properties", new JSONObject()
                            .put("symbol", new JSONObject()
                                    .put("type", "string")
                                    .put("description", "Any word")
                            )
                    )
                    .put("required", new JSONArray(rec_cos_array))

            );


    public String skin(String st, String sc1, String sc2, String category) throws JSONException {
        JSONObject userData = usersData.getJSONObject(stdid);
        String userskintype = userData.getString("skintype");
        Log.d("gpt_function", "Calling with st=" + userskintype + ", sc1=" + sc1 + " and sc2=" + sc2 + " and category=" + category);
        JSONObject object = new JSONObject();
        try {
            object.put("question", "Recommend 'three' random '"+ mymap.get(category) +"' which contain '" + userskintype + "' skin type and 'must contain' skin concerns 'BOTH' '" + sc1 + "' 'AND' '" + sc2 + "'.sample(3). Print it");
            Log.d("gpt_function", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://121.138.183.119:31002/recommend")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        String result = jsonObject.getString("result");
                        addResponse(result.trim());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 여기서 generateRelatedQuestions 호출
                                generateRelatedQuestions(userQuestions.get(userQuestions.size() - 1));
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    addResponse("Failed to load response due to " + response.body().string());
                }
            }
        });
        return "skin";
    }

    public String skin_st(String st) {
        // Uncomment the following lines if you want to read data from "cosmetics.csv"
        // DataFrame df = pd.read_csv("cosmetics.csv");
        // return df[(df['lululab_skintypes'].str.contains(st)) & (df['lululab_skin_concerns'].str.contains(sc1 | sc2))].sample(3);

        //logger.info("Calling with st=" + st + ", sc1=" + sc1 + " and sc2=" + sc2);
        Log.d("gpt_function", "Calling with st=" + st);
        return "skin_st";
    }


    public String recommend_cos(String symbol) throws JSONException {
        Log.d("gpt_function", "Calling with symbol=" + symbol);
        JSONObject userData = usersData.getJSONObject(stdid);
        String userskintype = userData.getString("skintype");
        JSONObject object = new JSONObject();
        try {
            object.put("question", "'"+ userskintype + "'피부타입이고 '" +userQuestions.get(userQuestions.size() - 1) +"' 에 맞는 화장품을 3개 추천해줘.sample(3)");
            Log.d("gpt_function", "userQuestions "+ userQuestions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://121.138.183.119:31002/recommend")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    String name = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        String result = jsonObject.getString("result");
                        result = result.replace("삵", "");

                        StringBuilder namesBuilder = new StringBuilder();

                        if(result.contains("|")){
                            String[] rows = result.split("\n");

                            for (int i = 2; i < rows.length; i++) { // 첫 번째와 두 번째 줄은 헤더이므로 건너뛰기
                                String[] columns = rows[i].split("\\|");
                                name = columns[columns.length -2].trim(); // name은 마지막 열 바로 앞에 있음
                                namesBuilder.append(i-1 + ". ");
                                namesBuilder.append(name);
                                namesBuilder.append("\n"); // 줄바꿈 문자를 추가
                            }

                            String allNames = namesBuilder.toString().trim(); // 마지막 줄바꿈 문자를 제거하기 위해 trim 사용
                            String answer = "사용자에 맞는 화장품을 추천하겠습니다.\n\n" + allNames;
                            Log.d("parse", answer);
                            addResponse(answer);
                        }
                        else{
                            addResponse(result);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 여기서 generateRelatedQuestions 호출
                                generateRelatedQuestions(userQuestions.get(userQuestions.size() - 1));
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    addResponse("Failed to load response due to " + response.body().string());
                }
            }
        });
        return "rec";
    }
    public String camera_rec(String cam) throws JSONException {
        String resp;
        JSONObject userData = usersData.getJSONObject(stdid);

        if(cameraFlag == 0)
        {
            resp = "자세한 분석을 위해서 카메라를 통해 사진을 찍어 보내주세요!";
            addResponse(resp);
        }
        else
        {
            tv_welcome.setVisibility(View.GONE);
            JSONArray arr = new JSONArray();
            JSONObject baseAi = new JSONObject();

            try {
                // AI 속성설정
                baseAi.put("role", "system");
                baseAi.put("content", "You are a helpful and kind AI Assistant.");
                // array로 담아서 한번에 보낸다
                arr.put(baseAi);

                JSONObject userMsg = new JSONObject();
                userMsg.put("role","user");
                userMsg.put("content",userData + "피부 분석요청이 오면 내 피부를 분석해주는데 다음 데이터를 기반으로 분석해줘. 피부 타입은  O : 지성 D : 건성 N : 정상 S : 민감성 R : 저항성 A : " +
                        "알러지성으로 나뉘어. 예를 들면 지성-민감성인 사람은 OS로 표시해. 또 피부를 각 항목에 따라 수치화해서 점수로 표현하는데 점수가 낮을수록 피부가 안좋아. 그 항목들에는 모공, 피지, 붉은기, 여드름, 주름, 색소침착," +
                        " 다크써클, 탄력, 생기, 화사함이 있어. 점수는 0점부터 10점까지야.  예를 들어 여드름이 1점인 사람은 여드름이 심각한 피부를 가지고 있어. 주름이 9점인 사람은 주름이 거의 없다는 뜻이야. 점수가 7점보다 낮으면 심각한 " +
                        "수준이라서 따로 관리가 필요하고 9점 이상인 항목은 따로 관리가 필요가 없는 항목이야. 6점 초과 9점 미만인 항목은 평범한 편이야. 점수가 높은 항목보다는 점수가 낮은 항목이 더 중요해.");
                arr.put(userMsg);


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            JSONObject object = new JSONObject();
            try {
                object.put("model", "gpt-3.5-turbo-0613");
                object.put("messages", arr);
                object.put("temperature", 0);
                object.put("max_tokens", 512);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(object.toString(), JSON);
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + MY_SECRET_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    addResponse("Failed to load response due to " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = null;
                        try {

                            jsonObject = new JSONObject(response.body().string());
                            JSONArray jsonArray = jsonObject.getJSONArray("choices");
                            String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                            Log.d("camera", "messageList " + messageList);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addResponse(result.trim());
                                    generateRelatedQuestions(userQuestions.get(userQuestions.size() - 1));
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        addResponse("Failed to load response due to " + response.body().string());
                    }
                }
            });


            //
        }

        Log.d("gpt_function", "Calling with cam=" + cam);
        return "rec";
    }

}