package com.example.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public class DashBoard extends AppCompatActivity {
    FloatingActionButton floatingButton;
    RecyclerView recview;
    myAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        floatingButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddPost.class));
            }
        });
        recview = (RecyclerView) findViewById(R.id.recView);
        recview.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<fileModel> options = new FirebaseRecyclerOptions.Builder<fileModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Posts"),fileModel.class)
                .build();

        adapter = new myAdapter(options);
        recview.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}