package com.example.ite303_finalproject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AlertDialog.Builder dialog;
    ArrayList<Note> obj;
    NoteAdapter adapter;
    ArrayAdapter<String> arrayAdapter;

    List<String> title_list;
    private DB_Note db;
    int item_Index = -1;
    String columnid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DB_Note(this); // initialize database

        fab();
        bindData();
        search();

    }

    private void addNote(String title, String description, String priority) {
        Note newNote = new Note(title, description, priority);
        obj.add(newNote);
        adapter.updateList(new ArrayList<>(obj));
        adapter.notifyItemInserted(obj.size() - 1);

        SQLiteDatabase obj = db.getWritableDatabase();
        ContentValues contents = new ContentValues();
        contents.put("Note_title", title);
        contents.put("Note_description", description);
        contents.put("Note_priority", priority);
        obj.insert("tblnote", null, contents);
        obj.close();
    }
//    implement update function and delete function
    private void updateNote(int position, String updatedTitle, String updatedDesc, String updatedPriority) {
        Note currentNote = obj.get(position);
        currentNote.setNote_title(updatedTitle);
        currentNote.setNote_description(updatedDesc);
        currentNote.setPriority(updatedPriority);

        obj.set(position, currentNote);
        adapter.updateList(new ArrayList<>(obj));

        SQLiteDatabase dbUpdate = db.getWritableDatabase();
        ContentValues contents = new ContentValues();
        contents.put("Note_title", updatedTitle);
        contents.put("Note_description", updatedDesc);
        contents.put("Note_priority", updatedPriority);



    }

    private void deleteNote(int position) {
        obj.remove(position);
        adapter.updateList(new ArrayList<>(obj));


    }

    public void displayDialog(){
        dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.note_gui, null);
        dialog.setView(view);
        AlertDialog alert = dialog.create();
        alert.show();

        EditText title = view.findViewById(R.id.editTextTitle);
        EditText dsc = view.findViewById(R.id.editTextDescription);
        Button submit = view.findViewById(R.id.buttonSave);
        RadioButton radioHigh = view.findViewById(R.id.radioHigh);
        RadioButton radioMedium = view.findViewById(R.id.radioMedium);
        RadioButton radioLow = view.findViewById(R.id.radioLow);

        submit.setOnClickListener(v -> {
            String nt = title.getText().toString();
            String nd = dsc.getText().toString();
            String priority = "";

            if (radioHigh.isChecked()) priority = "High";
            else if (radioMedium.isChecked()) priority = "Medium";
            else if (radioLow.isChecked()) priority = "Low";

            addNote(nt, nd, priority);
            alert.dismiss();
        });
    }

    public void showUpdateDialog(int position, Note currentNote) {
        AlertDialog.Builder updateDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.note_gui, null);
        updateDialog.setView(view);
        AlertDialog alertDialog = updateDialog.create();
        alertDialog.show();

        EditText title = view.findViewById(R.id.editTextTitle);
        EditText description = view.findViewById(R.id.editTextDescription);
        RadioButton radioHigh = view.findViewById(R.id.radioHigh);
        RadioButton radioMedium = view.findViewById(R.id.radioMedium);
        RadioButton radioLow = view.findViewById(R.id.radioLow);
        Button saveBtn = view.findViewById(R.id.buttonSave);

        title.setText(currentNote.getNote_title());
        description.setText(currentNote.getNote_description());

        switch (currentNote.getPriority()) {
            case "High": radioHigh.setChecked(true); break;
            case "Medium": radioMedium.setChecked(true); break;
            case "Low": radioLow.setChecked(true); break;
        }

        saveBtn.setText("Update");

        saveBtn.setOnClickListener(v -> {
            String updatedTitle = title.getText().toString();
            String updatedDesc = description.getText().toString();
            String updatedPriority = "";

            if (radioHigh.isChecked()) updatedPriority = "High";
            else if (radioMedium.isChecked()) updatedPriority = "Medium";
            else if (radioLow.isChecked()) updatedPriority = "Low";

            updateNote(position, updatedTitle, updatedDesc, updatedPriority);
            alertDialog.dismiss();
        });
    }

    private void showBottomSheetDialog(int position) {
        if (position < 0 || position >= obj.size()) return;

        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.show();

        Button btnUpdate = view.findViewById(R.id.btnUpdate);
        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnUpdate.setOnClickListener(v -> {
            showUpdateDialog(position, obj.get(position));
            dialog.dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            deleteNote(position);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    public void bindData() {
        obj = new ArrayList<>();
        obj.add(new Note("Do final project", "Do it for 10 mn", "High"));
        title_list = new ArrayList<>();
        title_list.add("Do final project");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NoteAdapter(obj, position -> showBottomSheetDialog(position));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void fab(){
        FloatingActionButton Fab = findViewById(R.id.fabAddNote);
        Fab.setOnClickListener(view -> displayDialog());
    }

    public void search() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Note> filteredList = new ArrayList<>();

                for (Note note : obj) {
                    if (note.getNote_title().toLowerCase().startsWith(newText.toLowerCase())) {
                        filteredList.add(note);
                    }
                }

                adapter.updateList(filteredList);
                return true;
            }
        });
    }
}
