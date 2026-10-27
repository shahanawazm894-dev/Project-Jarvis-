package com.shahanawaz.jarvis;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.graphics.Color;
import android.graphics.Typeface;
import java.util.ArrayList;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    private static final int SPEECH_REQUEST = 101;
    private TextView chat;
    private TextToSpeech tts;
    private boolean ready = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(1);
        root.setPadding(24, 32, 24, 24);
        root.setBackgroundColor(Color.rgb(5, 18, 27));
        TextView title = new TextView(this);
        title.setText("J.A.R.V.I.S");
        title.setTextSize(30);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.rgb(80, 220, 255));
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        chat = new TextView(this);
        chat.setTextColor(Color.WHITE);
        chat.setTextSize(18);
        chat.setPadding(8, 28, 8, 28);
        chat.setText("JARVIS: I am JARVIS, sir. Always at your service.");
        ScrollView scroll = new ScrollView(this);
        scroll.addView(chat);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        Button talk = new Button(this);
        talk.setText("TALK TO JARVIS");
        root.addView(talk);
        Button search = new Button(this);
        search.setText("SEARCH THE WEB");
        root.addView(search);
        setContentView(root);
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                ready = tts.setLanguage(new Locale("en", "IN")) != TextToSpeech.LANG_MISSING_DATA;
                tts.speak("I am JARVIS, sir. Always at your service.", TextToSpeech.QUEUE_FLUSH, null, "welcome");
            }
        });
        talk.setOnClickListener(v -> listen());
        search.setOnClickListener(v -> openSearch("AI assistant"));
    }
    private void listen() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to JARVIS");
        try { startActivityForResult(i, SPEECH_REQUEST); }
        catch (Exception e) { reply("Sir, voice recognition is unavailable."); }
    }
    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request == SPEECH_REQUEST && result == RESULT_OK && data != null) {
            ArrayList<String> words = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (words != null && !words.isEmpty()) command(words.get(0));
        }
    }
    private void command(String input) {
        chat.append("\n\nYOU: " + input);
        String q = input.toLowerCase(Locale.ROOT);
        if (q.contains("time") || q.contains("samay") || q.contains("baje")) {
            reply("Sir, the time is " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));
        } else if (q.contains("date") || q.contains("tareekh")) {
            reply("Sir, today is " + new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date()));
        } else if (q.contains("search") || q.contains("google") || q.contains("web")) {
            openSearch(input); reply("Opening web search, sir.");
        } else if (q.contains("hello") || q.contains("namaste") || q.contains("salaam")) {
            reply("Greetings, sir. How may I assist you?");
        } else { reply("Sir, I heard: " + input + ". This starter version supports basic commands and web search."); }
    }
    private void openSearch(String query) {
        Intent i = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com/search?q=" + android.net.Uri.encode(query)));
        try { startActivity(i); } catch (Exception e) { reply("Sir, browser could not be opened."); }
    }
    private void reply(String text) {
        chat.append("\n\nJARVIS: " + text);
        if (tts != null && ready) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "reply");
    }
    @Override
    protected void onDestroy() {
        if (tts != null) tts.shutdown();
        super.onDestroy();
    }
              }
