package com.abooc.plugin.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 14-09-05.
 * <p/>
 * 关于页面
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    public static void launch(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        About about = About.getAbout();
        TextView AppName = (TextView) findViewById(R.id.AppName);
        AppName.setText(about.AppName);
        TextView VersionView = (TextView) findViewById(R.id.Version);
        VersionView.setText(about.versionName + " (build:" + about.versionCode + ")");
        TextView Copyright = (TextView) findViewById(R.id.Copyright);
        Copyright.setText(about.copyright);
        ImageView logoView = (ImageView) findViewById(R.id.Logo);
        logoView.setImageResource(about.iconResId);


    }

    @Override
    protected void onResume() {
        super.onResume();
        View viewById = findViewById(R.id.Update);

        About about = About.getAbout();
        if (null == about.updateUrl || "".equals(about.updateUrl)) {
            viewById.setVisibility(View.GONE);
        } else {
            viewById.setOnClickListener(this);
            viewById.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 检查更新事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        About about = About.getAbout();
        Uri uri = Uri.parse(about.updateUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void onClickPoweredBy(View view) {
        String url = getResources().getString(R.string.plugin_about_powered_by_url);
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
