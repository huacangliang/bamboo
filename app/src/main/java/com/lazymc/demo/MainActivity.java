package com.lazymc.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lazymc.bamboo.Bamboo;
import com.lazymc.universalproxy.annotation.ProxyInject;

import java.io.File;
import java.io.IOException;

@ProxyInject
public class MainActivity extends Activity implements View.OnClickListener {

    private EditText mEtKey;
    private EditText mEtValue;
    private Button mBtWrite;
    private Button mBtRead;
    private Button mBtDel;
    private Bamboo bamboo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            bamboo = new Bamboo(new File(mkdir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bamboo"), "test.db"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            bamboo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mEtKey = (EditText) findViewById(R.id.et_key);
        mEtValue = (EditText) findViewById(R.id.et_value);
        mBtWrite = (Button) findViewById(R.id.bt_write);
        mBtRead = (Button) findViewById(R.id.bt_read);
        mBtDel = (Button) findViewById(R.id.bt_del);
        mBtWrite.setOnClickListener(this);
        mBtRead.setOnClickListener(this);
        mBtDel.setOnClickListener(this);
    }

    @Override
    public void onClick(View var1) {
        switch (var1.getId()) {
            case R.id.bt_write:
                write();
                break;
            case R.id.bt_read:
                read();
                break;
            case R.id.bt_del:
                del();
                break;
        }
    }

    private void del() {
        String key = mEtKey.getText().toString();
        if (checkKey(key)) return;
        try {
            if (bamboo.getBambooServer().cut(key)) {
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read() {
        String key = mEtKey.getText().toString();
        if (checkKey(key)) return;
        try {
            String value = bamboo.getBambooServer().read(key);
            if (TextUtils.isEmpty(value)) {
                Toast.makeText(this, "读取失败，没有该值", Toast.LENGTH_SHORT).show();
                return;
            }
            mEtValue.setText(value);
            Toast.makeText(this, "读取成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write() {
        String key = mEtKey.getText().toString();
        String value = mEtValue.getText().toString();
        if (checkKey(key)) return;
        try {
            if (bamboo.getBambooServer().write(key, value)) {
                Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "写入失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkKey(String key) {
        if (TextUtils.isEmpty(key)) {
            Toast.makeText(this, "key不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public File mkdir(String path) {
        try {
            File file = new File(path);
            if (file.exists() && file.isDirectory()) {
                return file;
            }
            if (file.mkdirs()) {
                return file;
            } else
                throw new RuntimeException();
        } catch (Exception e) {
            path = getFilesDir().getAbsolutePath() + "/" + path;
            File file = new File(path);
            file.mkdirs();
            return file;
        }
    }
}
