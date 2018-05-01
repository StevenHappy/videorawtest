package videorawtest.example.com.videorawtest;


import android.os.Bundle;
import android.preference.PreferenceFragment;


public class TestPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


}
