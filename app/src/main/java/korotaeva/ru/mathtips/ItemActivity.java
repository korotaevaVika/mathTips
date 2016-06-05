package korotaeva.ru.mathtips;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.webkit.*;

public class ItemActivity extends Activity {

    public static final String EXTRA_FILENAME = "file_name";

    public static Intent getIntent(Context context, String fileName) {
        Intent intent = new Intent(context, ItemActivity.class);
        intent.putExtra(EXTRA_FILENAME, fileName);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        String nameOfFile = getIntent().getStringExtra(EXTRA_FILENAME);

        WebView webView = (WebView) findViewById(R.id.page_web_view);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);

        webView.loadUrl("file:///android_asset/html/" + nameOfFile);
    }
}
