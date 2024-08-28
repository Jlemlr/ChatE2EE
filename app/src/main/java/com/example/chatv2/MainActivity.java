package com.example.chatv2;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.chatv2.Fragments.ChatFragment;
import com.example.chatv2.Fragments.UsersFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Model.User;

public class MainActivity extends AppCompatActivity {

    // pour ajouter pp voir https://www.youtube.com/watch?v=LyAmpfm4ndo&list=PLzLFqCABnRQftQQETzoVMuteXzNiXmnj8&index=4
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Button logoutbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        username = findViewById(R.id.usernameText);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://chatv2-4dee6-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null && user.getUsername() != null) {
                    username.setText(user.getUsername());
                } else {
                    username.setText("Unknown User");
                    Toast.makeText(MainActivity.this, "Username is not set or couldn't be loaded.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_LONG).show();
            }
        });


        logoutbt = findViewById(R.id.logoutButton);
        logoutbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finish();
            }
        });

        // Set up TabLayout and ViewPager2
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Set up the ViewPagerAdapter with fragments
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        // Add the fragments to the adapter
        viewPagerAdapter.addFragment(new ChatFragment(), "Chats");
        viewPagerAdapter.addFragment(new UsersFragment(), "Users");

        // Bind TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(viewPagerAdapter.getTitle(position));
        }).attach();

    }

    // ViewPagerAdapter class for managing fragments
    static class ViewPagerAdapter extends FragmentStateAdapter {

        private final ArrayList<Fragment> fragments = new ArrayList<>();
        private final ArrayList<String> titles = new ArrayList<>();

        // Constructor
        public ViewPagerAdapter(@NonNull FragmentActivity fa) {
            super(fa);
        }

        // Create the fragment based on the position
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        // Get the total number of fragments
        @Override
        public int getItemCount() {
            return fragments.size();
        }

        // Add a fragment and its title to the adapter
        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        // Get the title of the tab
        public String getTitle(int position) {
            return titles.get(position);
        }
    }
}