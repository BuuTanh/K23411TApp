package com.tranlebuutanh.k23411tapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tranlebuutanh.utils.VectorUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class MyUELResultActivity extends AppCompatActivity {

    static final String BASE_URL =
        "https://myuel.uel.edu.vn/Default.aspx?ModuleId=f92f39b2-dea3-4185-8cbb-56c1c49c5226" +
        "&DepartmentID=05&GraduateLevelID=DH&StudyTypeID=CQ&OlogyID=411";

    // Corpus mô tả từng ngành (keyword-rich) để vector hóa
    // Key = tên ngành, Value = corpus mô tả
    static LinkedHashMap<String, String[]> PROGRAM_DB = new LinkedHashMap<>();

    static {
        PROGRAM_DB.put("Kinh tế học", new String[]{"7310101","401",
            "kinh tế học vĩ mô vi mô kinh tế lượng phân tích thị trường chính sách kinh tế tăng trưởng"});
        PROGRAM_DB.put("Kinh tế và Quản lý công", new String[]{"403","403",
            "kinh tế quản lý công nhà nước chính sách công hành chính quản trị công cộng"});
        PROGRAM_DB.put("Kinh doanh quốc tế", new String[]{"408","408",
            "kinh doanh quốc tế thương mại xuất nhập khẩu ngoại thương đàm phán hợp đồng quốc tế"});
        PROGRAM_DB.put("Kinh tế đối ngoại", new String[]{"402","402",
            "kinh tế đối ngoại ngoại giao thương mại quốc tế xuất khẩu ngoại thương"});
        PROGRAM_DB.put("Công nghệ tài chính", new String[]{"414","414",
            "công nghệ tài chính fintech thanh toán số blockchain crypto tiền số ứng dụng tài chính"});
        PROGRAM_DB.put("Ngân hàng", new String[]{"412","412",
            "ngân hàng tín dụng cho vay tiết kiệm lãi suất tài chính ngân hàng thương mại trung ương"});
        PROGRAM_DB.put("Tài chính - Ngân hàng", new String[]{"404","404",
            "tài chính ngân hàng đầu tư chứng khoán quản lý tài sản phân tích tài chính"});
        PROGRAM_DB.put("Kế toán", new String[]{"405","405",
            "kế toán kiểm toán báo cáo tài chính sổ sách thuế kế toán doanh nghiệp IFRS"});
        PROGRAM_DB.put("Kiểm toán", new String[]{"409","409",
            "kiểm toán kiểm tra báo cáo tài chính rủi ro nội bộ kế toán kiểm toán viên"});
        PROGRAM_DB.put("Hệ thống thông tin quản lý", new String[]{"406","406",
            "hệ thống thông tin quản lý MIS ERP CRM cơ sở dữ liệu phân tích hệ thống quản lý doanh nghiệp SAP Oracle quy trình nghiệp vụ IT"});
        PROGRAM_DB.put("Kinh doanh số và Trí tuệ nhân tạo", new String[]{"416","416",
            "kinh doanh số trí tuệ nhân tạo AI machine learning deep learning big data python data science tự động hóa chatbot NLP robot"});
        PROGRAM_DB.put("Thương mại điện tử", new String[]{"411","411",
            "thương mại điện tử ecommerce bán hàng online marketing số digital shopee lazada tiki logistics thanh toán trực tuyến SEO"});
        PROGRAM_DB.put("Digital Marketing", new String[]{"417","417",
            "digital marketing marketing số SEO SEM content social media quảng cáo trực tuyến google facebook analytics"});
        PROGRAM_DB.put("Marketing", new String[]{"410","410",
            "marketing thị trường nghiên cứu người tiêu dùng thương hiệu chiến lược marketing mix quảng cáo"});
        PROGRAM_DB.put("Quản lý công", new String[]{"418","418",
            "quản lý công hành chính nhà nước chính sách công quản trị tổ chức công cộng"});
        PROGRAM_DB.put("Quản trị du lịch và lữ hành", new String[]{"415","415",
            "quản trị du lịch lữ hành khách sạn nhà hàng tour hướng dẫn viên du lịch hospitality"});
        PROGRAM_DB.put("Quản trị kinh doanh", new String[]{"407","407",
            "quản trị kinh doanh quản lý doanh nghiệp chiến lược tổ chức nhân sự marketing lãnh đạo"});
        PROGRAM_DB.put("Luật Dân sự", new String[]{"503","503",
            "luật dân sự hợp đồng tài sản thừa kế hôn nhân gia đình tranh chấp tòa án"});
        PROGRAM_DB.put("Luật Tài chính - Ngân hàng", new String[]{"504","504",
            "luật tài chính ngân hàng quy định pháp lý tài chính chứng khoán ngân hàng thuế"});
        PROGRAM_DB.put("Luật và Chính sách công", new String[]{"505","505",
            "luật chính sách công quy định nhà nước hành chính luật hành chính"});
        PROGRAM_DB.put("Luật kinh doanh", new String[]{"501","501",
            "luật kinh doanh doanh nghiệp hợp đồng thương mại tranh chấp kinh doanh pháp lý"});
        PROGRAM_DB.put("Luật thương mại quốc tế", new String[]{"502","502",
            "luật thương mại quốc tế WTO hiệp định thương mại xuất nhập khẩu trọng tài quốc tế"});
        PROGRAM_DB.put("Phân tích dữ liệu", new String[]{"419","419",
            "phân tích dữ liệu data analytics thống kê R python SQL dashboard báo cáo business intelligence BI"});
        PROGRAM_DB.put("Toán kinh tế", new String[]{"413","413",
            "toán kinh tế toán ứng dụng kinh tế lượng thống kê tối ưu hóa mô hình toán học tài chính"});
    }

    TextView txtQueryHeader, txtBestMatch, txtBestScore;
    TextView txtScore1, txtScore2, txtScore3;
    TextView txtResultContent, txtLoadingMsg;
    ProgressBar progressBar;
    Button btnBack;

    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myuel_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtQueryHeader   = findViewById(R.id.txtQueryHeader);
        txtBestMatch     = findViewById(R.id.txtBestMatch);
        txtBestScore     = findViewById(R.id.txtBestScore);
        txtScore1        = findViewById(R.id.txtScore1);
        txtScore2        = findViewById(R.id.txtScore2);
        txtScore3        = findViewById(R.id.txtScore3);
        txtResultContent = findViewById(R.id.txtResultContent);
        txtLoadingMsg    = findViewById(R.id.txtLoadingMsg);
        progressBar      = findViewById(R.id.progressBar);
        btnBack          = findViewById(R.id.btnBack);

        query = getIntent().getStringExtra("QUERY");
        if (query == null) query = "";
        String selectedProgram = getIntent().getStringExtra("SELECTED_PROGRAM");

        txtQueryHeader.setText("Từ khóa: \"" + query + "\"");
        btnBack.setOnClickListener(v -> finish());

        computeAndDisplay(selectedProgram);
    }

    private void computeAndDisplay(String selectedProgram) {
        String[] names   = PROGRAM_DB.keySet().toArray(new String[0]);
        String[] corpus  = new String[names.length];
        String[] oloIds  = new String[names.length];

        for (int i = 0; i < names.length; i++) {
            String[] entry = PROGRAM_DB.get(names[i]);
            oloIds[i]  = entry[0];
            corpus[i]  = entry[2];
        }

        // Vector hóa + cosine similarity (luôn tính để hiển thị top 3)
        double[] scores = VectorUtils.allScores(query, corpus);
        int[] top3 = topThree(scores);

        // Nếu user chọn ngành cụ thể từ suggestion → dùng ngành đó
        int bestIdx = top3[0];
        if (selectedProgram != null) {
            for (int i = 0; i < names.length; i++) {
                if (names[i].equals(selectedProgram)) { bestIdx = i; break; }
            }
        }

        txtBestMatch.setText("🏆 " + names[bestIdx]);
        txtBestScore.setText(String.format(Locale.US,
            "Cosine Similarity: %.4f  (thang 0–1)", scores[bestIdx]));

        txtScore1.setText(String.format(Locale.US,
            "🥇 %s: %.4f", names[top3[0]], scores[top3[0]]));
        txtScore2.setText(String.format(Locale.US,
            "🥈 %s: %.4f", names[top3[1]], scores[top3[1]]));
        txtScore3.setText(String.format(Locale.US,
            "🥉 %s: %.4f", names[top3[2]], scores[top3[2]]));

        fetchWebData(oloIds[bestIdx], names[bestIdx]);
    }

    private int[] topThree(double[] scores) {
        int[] idx = new int[scores.length];
        for (int i = 0; i < idx.length; i++) idx[i] = i;
        // Bubble sort top 3
        for (int i = 0; i < 3 && i < idx.length; i++) {
            for (int j = i + 1; j < idx.length; j++) {
                if (scores[idx[j]] > scores[idx[i]]) {
                    int tmp = idx[i]; idx[i] = idx[j]; idx[j] = tmp;
                }
            }
        }
        return new int[]{idx[0], Math.min(idx[1], scores.length-1), Math.min(idx[2], scores.length-1)};
    }

    private void fetchWebData(String ologyId, String programName) {
        progressBar.setVisibility(View.VISIBLE);
        txtLoadingMsg.setVisibility(View.VISIBLE);
        txtLoadingMsg.setText("⏳ Đang tải thông tin ngành \"" + programName + "\" từ MYUEL...");

        String url = "https://myuel.uel.edu.vn/Default.aspx?ModuleId=f92f39b2-dea3-4185-8cbb-" +
                     "56c1c49c5226&OlogyID=" + ologyId +
                     "&DepartmentID=05&GraduateLevelID=DH&StudyTypeID=CQ";

        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36")
                        .timeout(12000)
                        .get();

                // Lấy các môn học / thông tin chương trình
                StringBuilder sb = new StringBuilder();
                Elements tables = doc.select("table");
                if (!tables.isEmpty()) {
                    for (Element row : tables.first().select("tr")) {
                        String rowText = row.text().trim();
                        if (!rowText.isEmpty()) sb.append("• ").append(rowText).append("\n");
                    }
                }
                if (sb.length() == 0) {
                    // Fallback: lấy text toàn trang
                    String body = doc.body().text();
                    sb.append(body.length() > 1200 ? body.substring(0, 1200) + "..." : body);
                }

                final String content = sb.toString();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    txtLoadingMsg.setVisibility(View.GONE);
                    txtResultContent.setVisibility(View.VISIBLE);
                    txtResultContent.setText("📄 Chương trình đào tạo – " + programName + ":\n\n" + content);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    txtLoadingMsg.setText(
                        "⚠️ Không thể tải từ MYUEL (cần đăng nhập VPN hoặc mạng trường).\n" +
                        "✅ Kết quả vector hóa cosine similarity đã hiển thị bên trên.");
                });
            }
        }).start();
    }
}
