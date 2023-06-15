package com.example.learnenglish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnenglish.adapters.DefinitionAdapter;
import com.example.learnenglish.model.Definition;
import com.example.learnenglish.model.Meaning;
import com.example.learnenglish.model.Phonetic;
import com.example.learnenglish.model.Root;
import com.example.learnenglish.model.SearchedWord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText edt_wantedWord;
    private Button btn_search;
    private TextView txt_def;
    private TextView txt_example;
    private Button btn_hear;
    Root[] entity = new Root[1];
    private String audioUrl = "";
    private RecyclerView recycler;

    List<SearchedWord> searchedWords = new ArrayList();

    SharedPreferences localStorage;
    SharedPreferences.Editor editor;
    Gson jsonConverter = new Gson();

    // TODO: 6/12/2023  I added the addition of the newly searched word to the list method, and also the fetching to the list method, Now: make the recycler view and use the list to get the instances from it
    // last to-do is done
    // TODO: 6/15/2023 Check the last three methods, then use them across the application wherever they're needed.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localStorage = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        editor = localStorage.edit();
        edt_wantedWord = findViewById(R.id.edt_readWord);
        btn_search = findViewById(R.id.btn_search);
        txt_def = findViewById(R.id.txt_wordDef);
        txt_example = findViewById(R.id.txt_example);
        btn_hear = findViewById(R.id.btn_listen);
        recycler = findViewById(R.id.words_recycler);
        fetchSearchedWords();
        fetchCards();
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
                if (networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {    //check if phone is connected to internet first.
                    String word = edt_wantedWord.getText().toString();
                    if (word.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please type in a word", Toast.LENGTH_LONG).show();
                        txt_example.setText("");
                        txt_def.setText("");
                    } else {
                        try {
                            run(word);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Make sure you're connected to Wi-Fi or mobile data is turned on, then try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_hear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioUrl != "") {
                    try {
                        if (audioUrl == "noAudio")
                            Toast.makeText(getApplicationContext(), "No sound provided by the source.", Toast.LENGTH_LONG).show();
                        else
                            playAudio(audioUrl);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please search for a word first.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    void playAudio(String url) throws Exception {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {    //so the loading of the audio file won't block the app flow
                mp.start();
            }
        });
    }

    void run(String word) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.dictionaryapi.dev/api/v2/entries/en/" + word)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                JSONArray Jobject;
                String defintionSub = new String();
                try {
                    if (response.code() != 404) {
                        Jobject = new JSONArray(response.body().string());
                        ObjectMapper objectMapper = new ObjectMapper();
                        entity[0] = objectMapper.readValue(Jobject.get(0).toString(), Root.class);
                        defintionSub = entity[0].meanings[0].definitions[0].definition;  //this in case the retrieved object doesn't contain example, then the definition will be shown without example
                        Definition[] filteredDefinitions = Arrays.stream(entity[0].meanings)
                                .flatMap(meaning -> Arrays.stream(meaning.definitions))
                                .filter(def -> def.example != null)
                                .toArray(Definition[]::new);    //to remove any definition that has no example, but in case all of the received definitions don't include example, definitionSub already took a definition
                        Phonetic[] filteredPhonetics = Arrays.stream(entity[0].phonetics).filter(phonetic -> phonetic.audio != "").toArray(Phonetic[]::new);    //to remove any empty audio field
                        entity[0].phonetics = filteredPhonetics;
                        entity[0].meanings[0].definitions = filteredDefinitions;
                    }
                    String finalDefintionSub = defintionSub;
                    SearchedWord newWord = new SearchedWord(word, finalDefintionSub);
                    addToListAndSave(newWord);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.code() != 404) {
                                if (entity[0].phonetics.length == 0) {
                                    audioUrl = "noAudio";
                                } else
                                    audioUrl = entity[0].phonetics[0].audio;
                                Log.d("HEY", "HELLLOO");
                                Log.d("check", entity[0].toString());
                                if (entity[0].meanings[0].definitions.length == 0) {
                                    txt_def.setText(finalDefintionSub);
                                    txt_example.setText("No example provided");
                                } else {
                                    txt_def.setText(entity[0].meanings[0].definitions[0].definition);
                                    txt_example.setText(entity[0].meanings[0].definitions[0].example);
                                }
                                fetchCards();
                            } else {
                                txt_def.setText("No definition Found");
                                audioUrl = "noAudio";
                                txt_example.setText("");
                            }
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);

                }

            }
        });
    }

    public void addToListAndSave(SearchedWord newWord) {
        searchedWords.add(newWord);
        String list = jsonConverter.toJson(searchedWords);
        editor.putString("List", list);
        Log.d("Add and Save", list);
        Log.d("State of saving", String.valueOf(editor.commit()));
    }

    public void fetchSearchedWords() {
        Type searchedWordListType = new TypeToken<List<SearchedWord>>() {
        }.getType();
        String jsonWords = localStorage.getString("List", "Not found");
        Log.d("fetch searched to list", jsonWords);
        if(!jsonWords.equals("Not found"))
            searchedWords = jsonConverter.fromJson(jsonWords, searchedWordListType);
    }

    public void fetchCards(){
        Log.d("start of cards fetch", "I'm in");
        Log.d("State of storage persistence", localStorage.getString("List", "Not found"));
        String[] cardWords = new String[searchedWords.size()];
        String[] cardDefinitions = new String[searchedWords.size()];
        for(int i = 0; i < searchedWords.size(); i++){
            cardDefinitions[i] = searchedWords.get(i).getDefinition();
            cardWords[i] = searchedWords.get(i).getWord();
        }
        recycler.setLayoutManager(new LinearLayoutManager(this));
        DefinitionAdapter adapter = new DefinitionAdapter(cardWords, cardDefinitions);
        recycler.setAdapter(adapter);
        Log.d("End of cards fetch", "I'm out");
    }

}


