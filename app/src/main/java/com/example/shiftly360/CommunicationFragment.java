package com.example.shiftly360;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CommunicationFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton newChatFab;
    private final String[] tabTitles = new String[]{"Chats", "Announcements"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communication, container, false);

        // Initialize views
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        newChatFab = view.findViewById(R.id.new_chat_fab);

        // Set up ViewPager
        CommunicationPagerAdapter pagerAdapter = new CommunicationPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // Set up FAB visibility based on selected tab
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                newChatFab.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            }
        });

        // Set up FAB
        newChatFab.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), NewChatActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
} 