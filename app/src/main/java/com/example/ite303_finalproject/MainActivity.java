package com.example.ite303_finalproject;

import android.content.ContentValues;
import android.database.Cursor;
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

    List<String> title_list;
    private DB_Note db;
    int item_Index = -1;
    private String columnid;

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


        create_Database();
        fab();
        bindData();
        search();
        getAllNotes();

    }
    public void create_Database(){
        db= new DB_Note(MainActivity.this);
    }

    private void addNote(String title, String description, String priority) {
        SQLiteDatabase dbWrite = db.getWritableDatabase();

        ContentValues contents = new ContentValues();
        contents.put(DB_Note.Col_title, title);
        contents.put(DB_Note.Col_description, description);
        contents.put(DB_Note.Col_priority, priority);

        long id = dbWrite.insert("tblnote", null, contents);
        dbWrite.close();

        Note newNote = new Note((int) id, title, description, priority);
        obj.add(newNote);
        adapter.updateList(new ArrayList<>(obj));
        adapter.notifyItemInserted(obj.size() - 1);
    }

public void updateNote(int id, String newTitle, String newDesc, String newPriority) {
    if (item_Index != -1) {
        Note updatedNote = new Note(id,newTitle, newDesc, newPriority);
        updatedNote.setId(Integer.parseInt(columnid));

        obj.set(item_Index, updatedNote);
        adapter.updateList(new ArrayList<>(obj));
        adapter.notifyDataSetChanged();

        SQLiteDatabase dbUpdate = db.getWritableDatabase();
        ContentValues contents = new ContentValues();
        contents.put(DB_Note.Col_title, newTitle);
        contents.put(DB_Note.Col_description, newDesc);
        contents.put(DB_Note.Col_priority, newPriority);

        dbUpdate.update("tblnote", contents, "id=?", new String[]{columnid});
        dbUpdate.close();

        item_Index = -1;
        columnid = "";
    }
}

    private void deleteNote(int position) {
        Note note = obj.get(position); // Get the note to delete

        // Remove from the database using the note's id
        SQLiteDatabase dbDelete = db.getWritableDatabase();
        String whereClause = "id = ?";
        String[] whereArgs = { String.valueOf(note.getId()) };
        dbDelete.delete(DB_Note.Tbl_name, whereClause, whereArgs);
        dbDelete.close();

        // Remove from the list and update the adapter
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
        item_Index = position;
        columnid = String.valueOf(obj.get(position).getId());
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
            int id = currentNote.getId(); //
            String updatedTitle = title.getText().toString();
            String updatedDesc = description.getText().toString();
            String updatedPriority = "";

            if (radioHigh.isChecked()) updatedPriority = "High";
            else if (radioMedium.isChecked()) updatedPriority = "Medium";
            else if (radioLow.isChecked()) updatedPriority = "Low";

            updateNote(id,updatedTitle, updatedDesc, updatedPriority);
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
        obj = getAllNotes();
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

    private ArrayList<Note> getAllNotes() {
        ArrayList<Note> notes = new ArrayList<>();
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("SELECT * FROM " + DB_Note.Tbl_name, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DB_Note.Col_id));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DB_Note.Col_title));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DB_Note.Col_description));
                String priority = cursor.getString(cursor.getColumnIndexOrThrow(DB_Note.Col_priority));

                notes.add(new Note(id, title, description, priority));
            } while (cursor.moveToNext());
        }

        cursor.close();
        dbRead.close();
        return notes;
    }
}
