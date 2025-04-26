package com.example.ite303_finalproject;

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
    ArrayList<Note>obj;
    NoteAdapter adapter;
    ArrayAdapter<String> arrayAdapter;

    List<String> title_list;




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
        fab();
        bindData();
        search();

    }


    // fix displayDialog method

    // the data doesn't display in the recycleView 
    public void displayDialog(){
        dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.note_gui, null);
        dialog.setView(view);
        dialog.show();

        EditText title = view.findViewById(R.id.editTextTitle);
        EditText dsc = view.findViewById(R.id.editTextDescription);
        Button submit = view.findViewById(R.id.buttonSave);
        RadioButton radioHigh = view.findViewById(R.id.radioHigh);
        RadioButton radioMedium = view.findViewById(R.id.radioMedium);
        RadioButton radioLow = view.findViewById(R.id.radioLow);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle save

                String nt = title.getText().toString();
                String nd = dsc.getText().toString();
                String priority = "";
                if (radioHigh.isChecked()) {
                    priority = "High";
                } else if (radioMedium.isChecked()) {
                    priority = "Medium";
                } else if (radioLow.isChecked()) {
                    priority = "Low";
                }
                Note newNote = new Note(nt, nd, priority);
                obj.add(newNote);
                adapter.updateList(new ArrayList<>(obj));
                adapter.notifyItemInserted(obj.size() - 1); // notify adapter of new item
                dialog.create().dismiss();
                adapter.notifyDataSetChanged();


            }
        });
    }

    public void bindData() {
        obj = new ArrayList<>();
        obj.add(new Note("Do final project", "Do it for 10 mn", "High"));
        title_list = new ArrayList<>();
        title_list.add("Do final project");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Pass the click listener to the adapter
        adapter = new NoteAdapter(obj, position -> showBottomSheetDialog(position));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    public void fab(){
        FloatingActionButton Fab = findViewById(R.id.fabAddNote);
        Fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog();
            }
        });
    }



    // create a title list
    // add the title from the obj to the title list
    // Create an arrayAdapter
    // Create a search function that search through the tittle list




    // Your search function does not work yet 

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
                    if (note.getNote_title().toLowerCase().startsWith(newText.toLowerCase()))
                    {
                        filteredList.add(note);
                    }
                }

                adapter.updateList(filteredList);
                return true;
            }
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

        // Pre-fill existing data
        title.setText(currentNote.getNote_title());
        description.setText(currentNote.getNote_description());

        switch (currentNote.getPriority()) {
            case "High":
                radioHigh.setChecked(true);
                break;
            case "Medium":
                radioMedium.setChecked(true);
                break;
            case "Low":
                radioLow.setChecked(true);
                break;
        }

        saveBtn.setText("Update"); // Optional: Change button text

        saveBtn.setOnClickListener(v -> {
            String updatedTitle = title.getText().toString();
            String updatedDesc = description.getText().toString();
            String updatedPriority = "";

            if (radioHigh.isChecked()) updatedPriority = "High";
            else if (radioMedium.isChecked()) updatedPriority = "Medium";
            else if (radioLow.isChecked()) updatedPriority = "Low";

            // Update the note object
            currentNote.setNote_title(updatedTitle);
            currentNote.setNote_description(updatedDesc);
            currentNote.setPriority(updatedPriority);

            obj.set(position, currentNote);
            adapter.updateList(new ArrayList<>(obj)); // or adapter.notifyItemChanged(position)
            alertDialog.dismiss();
        });
    }




    private void showBottomSheetDialog(int position) {
        if (position < 0 || position >= obj.size()) return; // Guard clause

        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.show();

        Button btnUpdate = view.findViewById(R.id.btnUpdate);
        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnUpdate.setOnClickListener(v -> {
            showUpdateDialog(position, obj.get(position)); // Pass current note
            dialog.dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            obj.remove(position);                          // Remove item
            adapter.updateList(new ArrayList<>(obj));      // Refresh list
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss()); // Just close the dialog
    }





}