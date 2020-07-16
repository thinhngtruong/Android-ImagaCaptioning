package com.example.luanvan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public final static int REQUEST_SELECT_FILE = 100;
    private final static int TAKE_PICTURE = 2;
    private static WebView simpleWebView;
    private Uri picUri;
    private static final String URL = "http://192.168.1.5:5000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        simpleWebView = (WebView) findViewById(R.id.simpleWebView);
        simpleWebView.loadUrl(URL);

        WebSettings mWebSettings = simpleWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(false);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowContentAccess(true);
        mWebSettings.setDomStorageEnabled(true);

        simpleWebView.setWebChromeClient(new WebChromeClient()
        {
            // For Lollipop 5.0+ Devices
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
//                uploadMessage = filePathCallback;
//                Method m = null;
//                try {
//                    m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    m.invoke(null);
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//                Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
//                File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pic.jpg");
//                picUri = Uri.fromFile(photo);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
//                startActivityForResult(cameraIntent, TAKE_PICTURE);
//                return true;

                Intent intent = fileChooserParams.createIntent();
                try
                {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e)
                {
                    uploadMessage = null;
                    Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

        });

        simpleWebView.setWebViewClient(new WebViewClient());
    }

    @Override
    public void onBackPressed() {
        WebView simpleWebView = (WebView) findViewById(R.id.simpleWebView);
        if (simpleWebView.canGoBack()) {
            simpleWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (requestCode == REQUEST_SELECT_FILE)
            {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
            else if (requestCode == TAKE_PICTURE) {
                if(resultCode == Activity.RESULT_OK){
                Uri result = intent == null ? this.picUri : intent.getData();
                uploadMessage.onReceiveValue(new Uri[]{result});
                uploadMessage = null;
                }
            }
        }
        else
            Toast.makeText(getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }
}


