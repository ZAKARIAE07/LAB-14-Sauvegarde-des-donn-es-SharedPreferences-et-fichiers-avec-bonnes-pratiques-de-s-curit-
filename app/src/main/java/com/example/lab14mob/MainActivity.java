package com.example.lab14mob;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab14mob.cache.CacheStore;
import com.example.lab14mob.external.ExternalAppFilesStore;
import com.example.lab14mob.files.InternalTextStore;
import com.example.lab14mob.files.StudentsJsonStore;
import com.example.lab14mob.model.Student;
import com.example.lab14mob.prefs.AppPrefs;
import com.example.lab14mob.prefs.SecurePrefs;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SecureStorageJava";
    private static final String EXTERNAL_FILE_NAME = "export.txt";
    private final List<String> langs = Arrays.asList("fr", "en", "ar");

    private EditText etName;
    private EditText etToken;
    private Spinner spLang;
    private SwitchCompat swDark;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etToken = findViewById(R.id.etToken);
        spLang = findViewById(R.id.spLang);
        swDark = findViewById(R.id.swDark);
        tvResult = findViewById(R.id.tvResult);

        setupLangSpinner();

        Button btnSavePrefs = findViewById(R.id.btnSavePrefs);
        Button btnLoadPrefs = findViewById(R.id.btnLoadPrefs);
        Button btnSaveJson = findViewById(R.id.btnSaveJson);
        Button btnLoadJson = findViewById(R.id.btnLoadJson);
        Button btnExportExternal = findViewById(R.id.btnExportExternal);
        Button btnLoadExternal = findViewById(R.id.btnLoadExternal);
        Button btnClear = findViewById(R.id.btnClear);

        btnSavePrefs.setOnClickListener(v -> savePrefs());
        btnLoadPrefs.setOnClickListener(v -> loadPrefsToUi());
        btnSaveJson.setOnClickListener(v -> saveJsonFile());
        btnLoadJson.setOnClickListener(v -> loadJsonFile());
        btnExportExternal.setOnClickListener(v -> exportToExternal());
        btnLoadExternal.setOnClickListener(v -> loadFromExternal());
        btnClear.setOnClickListener(v -> clearAll());

        loadPrefsToUi();
    }

    private void setupLangSpinner() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, langs);
        spLang.setAdapter(adapter);
    }

    private void savePrefs() {
        String name = etName.getText().toString().trim();
        String lang = langs.get(Math.max(0, spLang.getSelectedItemPosition()));
        String theme = swDark.isChecked() ? "dark" : "light";

        boolean ok = AppPrefs.save(this, name, lang, theme, false);

        String token = etToken.getText().toString();
        if (!token.isEmpty()) {
            try {
                SecurePrefs.saveToken(this, token);
            } catch (Exception e) {
                tvResult.setText(getString(R.string.error_token_encryption, e.getMessage()));
                return;
            }
        }

        Log.d(TAG, "Prefs sauvegardées ok=" + ok + ", name=" + name + ", lang=" + lang + ", theme=" + theme);

        try {
            CacheStore.write(this, "last_ui.txt", "name=" + name + ", lang=" + lang + ", theme=" + theme);
        } catch (Exception ignored) {}

        tvResult.setText(getString(R.string.save_prefs_success, name, lang, theme));
    }

    private void loadPrefsToUi() {
        AppPrefs.Triple triple = AppPrefs.load(this);

        etName.setText(triple.name);
        swDark.setChecked("dark".equals(triple.theme));

        int idx = langs.indexOf(triple.lang);
        spLang.setSelection(Math.max(0, idx));

        int tokenLen = 0;
        try {
            String token = SecurePrefs.loadToken(this);
            tokenLen = token.length();
        } catch (Exception ignored) {}

        tvResult.setText(getString(R.string.load_prefs_success, triple.name, triple.lang, triple.theme, tokenLen));

        Log.d(TAG, "Prefs chargées name=" + triple.name + ", lang=" + triple.lang + ", theme=" + triple.theme + ", tokenLength=" + tokenLen);
    }

    private void saveJsonFile() {
        List<Student> students = Arrays.asList(
                new Student(1, "Amina", 20),
                new Student(2, "Omar", 21),
                new Student(3, "Sara", 19)
        );

        try {
            StudentsJsonStore.save(this, students);
            InternalTextStore.writeUtf8(this, "note.txt", "Sauvegarde JSON effectuée (UTF-8).");
        } catch (Exception e) {
            tvResult.setText(getString(R.string.error_save_json, e.getMessage()));
            return;
        }

        Log.d(TAG, "Fichiers internes écrits: students.json, note.txt");
        tvResult.setText(getString(R.string.save_json_success, students.size()));
    }

    private void loadJsonFile() {
        List<Student> students = StudentsJsonStore.load(this);

        String note;
        try {
            note = InternalTextStore.readUtf8(this, "note.txt");
        } catch (Exception e) {
            note = getString(R.string.note_absent);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.load_json_success_header, note, students.size()));
        for (Student s : students) {
            sb.append(getString(R.string.student_item_format, s.id, s.name, s.age));
        }

        tvResult.setText(sb.toString());
        Log.d(TAG, "Fichier JSON chargé: students=" + students.size());
    }

    private void exportToExternal() {
        try {
            String content = "Export des données utilisateur : " + etName.getText().toString();
            String path = ExternalAppFilesStore.write(this, EXTERNAL_FILE_NAME, content);
            tvResult.setText(getString(R.string.export_external_success, path));
            Log.d(TAG, "Export externe réussi : " + path);
        } catch (Exception e) {
            tvResult.setText(getString(R.string.error_external, e.getMessage()));
        }
    }

    private void loadFromExternal() {
        try {
            String content = ExternalAppFilesStore.read(this, EXTERNAL_FILE_NAME);
            if (content == null) {
                tvResult.setText(getString(R.string.external_file_absent));
            } else {
                tvResult.setText(getString(R.string.load_external_success, content));
            }
        } catch (Exception e) {
            tvResult.setText(getString(R.string.error_external, e.getMessage()));
        }
    }

    private void clearAll() {
        AppPrefs.clear(this);

        try {
            SecurePrefs.clear(this);
        } catch (Exception ignored) {}

        StudentsJsonStore.delete(this);
        InternalTextStore.delete(this, "note.txt");
        ExternalAppFilesStore.delete(this, EXTERNAL_FILE_NAME);

        int purged = CacheStore.purge(this);

        etName.setText("");
        etToken.setText("");
        swDark.setChecked(false);
        spLang.setSelection(0);

        tvResult.setText(getString(R.string.clear_success, purged));

        Log.d(TAG, "Nettoyage terminé (aucune donnée sensible loggée).");
    }
}
