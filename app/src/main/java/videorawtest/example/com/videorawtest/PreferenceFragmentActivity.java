package videorawtest.example.com.videorawtest;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PreferenceFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_fragment);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        TestPreferenceFragment fragmentPreferences = new TestPreferenceFragment();
        transaction.replace(R.id.fragmentcontent, fragmentPreferences);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
