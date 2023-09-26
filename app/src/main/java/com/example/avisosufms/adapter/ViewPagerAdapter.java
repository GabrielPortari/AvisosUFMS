package com.example.avisosufms.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.avisosufms.fragment.AlunosFragment;
import com.example.avisosufms.fragment.ProfessoresFragment;
import com.example.avisosufms.fragment.SecretariaFragment;
import com.example.avisosufms.fragment.TodosFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new TodosFragment();
            case 1:
                return new SecretariaFragment();
            case 2:
                return new ProfessoresFragment();
            case 3:
                return new AlunosFragment();
            default:
                return new TodosFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
