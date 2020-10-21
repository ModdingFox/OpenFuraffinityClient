package open.furaffinity.client.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import open.furaffinity.client.R;

public class userActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user);
    }
}