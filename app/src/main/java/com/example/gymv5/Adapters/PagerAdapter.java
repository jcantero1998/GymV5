package com.example.gymv5.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.gymv5.Fragments.ClockFragment;
import com.example.gymv5.Fragments.ListFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int numberOfTabs;

    public static ListFragment listFragment = new ListFragment();
    public static ClockFragment clockFragment = new ClockFragment();

    // Extiendo de FragmentStatePagerAdapter
    // y creo el constructor y el resto de métodos obligatorios

    // En el constructor lo personalizamos añadiendo el número de tabs y lo
    // guardamos en una variable
    public PagerAdapter(FragmentManager fragmentManager, int numberOfTabs) {
        super(fragmentManager);
        this.numberOfTabs = numberOfTabs;
    }

    // Llega una posición, este evento se lanza cada vez que hacemos click
    // o cambiamos de tab
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return listFragment;
            case 1:
                return clockFragment;
            default:
                return null;
        }
    }

    //Para decir cuántos tabs tenemos
    @Override
    public int getCount() {
        return numberOfTabs;
    }
}