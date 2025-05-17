package com.example.shiftly360;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CommunicationPagerAdapter extends FragmentStateAdapter {

    public CommunicationPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the appropriate fragment based on position
        if (position == 0) {
            return new ChatsFragment();
        } else {
            return new AnnouncementsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of tabs
    }
} 