package com.tranlebuutanh.k23411tapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tranlebuutanh.utils.VectorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MyUELSearchActivity extends AppCompatActivity {

    EditText edtQuery;
    ImageButton btnVoice;
    TextView txtVoiceStatus, txtSuggestionLabel;
    ListView listSuggestions;
    View layoutEmpty;

    ActivityResultLauncher<Intent> voiceLauncher;
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable searchRunnable;

    // Lấy danh sách tên ngành từ PROGRAM_DB
    String[] programNames;
    String[] programCorpus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myuel_search);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtQuery          = findViewById(R.id.edtQuery);
        btnVoice          = findViewById(R.id.btnVoice);
        txtVoiceStatus    = findViewById(R.id.txtVoiceStatus);
        txtSuggestionLabel= findViewById(R.id.txtSuggestionLabel);
        listSuggestions   = findViewById(R.id.listSuggestions);
        layoutEmpty       = findViewById(R.id.layoutEmpty);

        // Chuẩn bị corpus từ PROGRAM_DB
        programNames  = MyUELResultActivity.PROGRAM_DB.keySet().toArray(new String[0]);
        programCorpus = new String[programNames.length];
        for (int i = 0; i < programNames.length; i++) {
            programCorpus[i] = MyUELResultActivity.PROGRAM_DB.get(programNames[i])[2];
        }

        // Voice launcher
        voiceLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> results = result.getData()
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (results != null && !results.isEmpty()) {
                        String recognized = results.get(0);
                        edtQuery.setText(recognized);
                        edtQuery.setSelection(recognized.length());
                        showVoiceStatus("🎙️ Đã nhận: \"" + recognized + "\"");
                    }
                } else {
                    showVoiceStatus("❌ Không nhận được giọng nói");
                }
            }
        );

        btnVoice.setOnClickListener(v -> startVoiceRecognition());

        // TextWatcher: debounce 300ms rồi compute suggestions
        edtQuery.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                handler.removeCallbacks(searchRunnable);
                String q = s.toString().trim();
                if (q.isEmpty()) {
                    showEmpty();
                    return;
                }
                searchRunnable = () -> updateSuggestions(q);
                handler.postDelayed(searchRunnable, 250);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Click suggestion → open result
        listSuggestions.setOnItemClickListener((parent, view, pos, id) -> {
            SuggestionItem item = (SuggestionItem) parent.getItemAtPosition(pos);
            doSearch(edtQuery.getText().toString().trim(), item.name);
        });

        // IME search action
        edtQuery.setOnEditorActionListener((v, actionId, event) -> {
            String q = edtQuery.getText().toString().trim();
            if (!q.isEmpty()) doSearch(q, null);
            return true;
        });
    }

    private void updateSuggestions(String query) {
        double[] scores = VectorUtils.allScores(query, programCorpus);

        // Build index array sorted by score desc
        Integer[] idx = new Integer[scores.length];
        for (int i = 0; i < idx.length; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Double.compare(scores[b], scores[a]));

        // Take top 5
        ArrayList<SuggestionItem> items = new ArrayList<>();
        for (int rank = 0; rank < Math.min(5, idx.length); rank++) {
            int i = idx[rank];
            items.add(new SuggestionItem(rank + 1, programNames[i], scores[i]));
        }

        layoutEmpty.setVisibility(View.GONE);
        txtSuggestionLabel.setVisibility(View.VISIBLE);
        listSuggestions.setVisibility(View.VISIBLE);
        listSuggestions.setAdapter(new SuggestionAdapter(this, items));
    }

    private void showEmpty() {
        txtSuggestionLabel.setVisibility(View.GONE);
        listSuggestions.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void showVoiceStatus(String msg) {
        txtVoiceStatus.setText(msg);
        txtVoiceStatus.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> txtVoiceStatus.setVisibility(View.GONE), 3000);
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói tên ngành bạn muốn tìm...");
        try {
            showVoiceStatus("🎙️ Đang lắng nghe...");
            voiceLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Thiết bị không hỗ trợ nhận diện giọng nói", Toast.LENGTH_SHORT).show();
        }
    }

    private void doSearch(String query, String selectedProgram) {
        Intent intent = new Intent(MyUELSearchActivity.this, MyUELResultActivity.class);
        intent.putExtra("QUERY", query);
        if (selectedProgram != null) intent.putExtra("SELECTED_PROGRAM", selectedProgram);
        startActivity(intent);
    }

    // ---- Data model ----
    static class SuggestionItem {
        int rank;
        String name;
        double score;
        SuggestionItem(int rank, String name, double score) {
            this.rank = rank; this.name = name; this.score = score;
        }
    }

    // ---- Adapter ----
    static class SuggestionAdapter extends ArrayAdapter<SuggestionItem> {
        SuggestionAdapter(Context ctx, ArrayList<SuggestionItem> items) {
            super(ctx, R.layout.item_suggestion, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_suggestion, parent, false);

            SuggestionItem item = getItem(position);
            TextView txtRank   = convertView.findViewById(R.id.txtRank);
            TextView txtName   = convertView.findViewById(R.id.txtProgramName);
            TextView txtBar    = convertView.findViewById(R.id.txtScoreBar);
            TextView txtScore  = convertView.findViewById(R.id.txtScore);

            // Rank medal
            String[] medals = {"🥇","🥈","🥉","4","5"};
            txtRank.setText(item.rank <= 3 ? medals[item.rank-1] : String.valueOf(item.rank));
            txtRank.setBackgroundColor(item.rank == 1 ? 0xFF1565C0 : 0xFF9E9E9E);

            txtName.setText(item.name);

            // Progress bar bằng ký tự ▓
            int bars = (int)(item.score * 20);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 20; i++) sb.append(i < bars ? "▓" : "░");
            txtBar.setText(sb.toString());

            txtScore.setText(String.format(Locale.US, "%.2f%%", item.score * 100));
            // Color chip by score
            if (item.score > 0.3) {
                txtScore.setTextColor(0xFF1B5E20);
                txtScore.setBackgroundColor(0xFFE8F5E9);
            } else if (item.score > 0.1) {
                txtScore.setTextColor(0xFFE65100);
                txtScore.setBackgroundColor(0xFFFFF3E0);
            } else {
                txtScore.setTextColor(0xFF757575);
                txtScore.setBackgroundColor(0xFFF5F5F5);
            }

            return convertView;
        }
    }
}
