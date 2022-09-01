package com.huozige.lab.container;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.huozige.lab.container.app.HzgJsBridgeApp;
import com.huozige.lab.container.compatible.HzgJsBridgeIndex;
import com.huozige.lab.container.pda.HzgJsBridgePDA;

/**
 * 主Activity，主要负责加载浏览器内核
 * 也需要作为其他功能的默认上下文
 */
public class MainActivity extends AppCompatActivity {

    WebView _webView; // 浏览器内核

    BaseBridge[] _bridges; // 需要嵌入页面的JS桥

    HzgWebViewClient _webViewClient; // 页面事件处理器

    HzgWebChromeClient _webChromeClient; // 浏览器事件处理器

    ConfigBroadcastReceiver _configRev = new ConfigBroadcastReceiver(); // 配置监听器

    static final int MENU_ID_HOME = 0;
    static final int MENU_ID_REFRESH = 1;
    static final int MENU_ID_ABOUT = 2;

    static final String PREFERENCE_NAME = "MAIN";
    static final String PREFERENCE_KEY_ENTRY = "ENTRY";

    private String getEntryUrl() {
        // 从配置库中读取启动地址
        SharedPreferences sharedPref = getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        return sharedPref.getString(PREFERENCE_KEY_ENTRY, getString(R.string.app_default_entry));
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Step 1. 设置页面标题
        setTitle(getString(R.string.ui_title_loading));

        // Step 2. 初始化浏览器内核
        // 2.1 创建浏览器内核
        _webView = new WebView(getApplicationContext());
        setContentView(_webView);

        // 2.2 通过WebSettings设置策略
        WebSettings settings = _webView.getSettings();

        // 权限
        settings.setJavaScriptEnabled(true); // 执行JS
        settings.setAllowFileAccess(true); // 访问缓存、本地数据库等文件
        settings.setAllowContentAccess(true); // 访问本地的所有文件，需配合权限设置
        settings.setAllowFileAccessFromFileURLs(true); // 通过URL访问网上的文件
        settings.setGeolocationEnabled(true); // 允许获取地理位置
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);  // 允许混用HTTP和HTTPS

        // 缓存
        settings.setDomStorageEnabled(true); // DOM缓存
        settings.setAppCacheEnabled(true); // 数据缓存（活字格采用的本地库就是这个）
        settings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath()); // 数据缓存路径

        // 布局
        settings.setUseWideViewPort(true); // 默认全屏

        // 缩放
        settings.setBuiltInZoomControls(false); // 不显示缩放按钮
        settings.setSupportZoom(false); // 不允许缩放

        // 2.3 默认设置焦点
        _webView.requestFocusFromTouch();

        // 2.4 加载页面
        _webView.loadUrl(getEntryUrl());

        // Step 3. 设置WebViewClient，处理页面事件
        _webViewClient = new HzgWebViewClient(this);
        _webView.setWebViewClient(_webViewClient);

        // Step 4. 设置WebChromeClient，处理浏览器事件
        _webChromeClient = new HzgWebChromeClient(this);
        _webChromeClient.RegistryLaunchersOnCreated(); // 初始化以当前Activity为上下文的启动器，这一操作仅允许在当前阶段调用，否则会出错
        _webView.setWebChromeClient(_webChromeClient);

        // Step 5. 设置JS桥

        // 5.1 创建需要嵌入页面的JS桥
        _bridges = new BaseBridge[]{
                // 创建默认的JS桥
                new HzgJsBridgeIndex(this, _webView),
                new HzgJsBridgePDA(this, _webView),
                new HzgJsBridgeApp(this, _webView)

                // 你可以在此定义和处理新的JS桥
        };

        // 5.2 依次处理JS桥
        for (BaseBridge br : _bridges
        ) {
            br.InitOnActivityCreated(); // 初始化以当前Activity为上下文的启动器，这一操作仅允许在当前阶段调用，否则会出错
            _webView.addJavascriptInterface(br, br.GetName()); // 将JS桥嵌入页面
        }

        // Step 6. 注册配置变更广播
        // 新版本Android不允许在配置中注册广播，必须和上下文绑定：https://developer.android.com/guide/components/broadcasts#context-registered-recievers
        // 这里的做法是绑定到应用上下文，寿命长于当前页面
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.app_config_broadcast_filter));
        getApplicationContext().registerReceiver(_configRev, filter);
    }

    /**
     * 销毁前的操作：取消监听器
     */
    @Override
    public void onDestroy() {

        // 取消广播监听
        getApplicationContext().unregisterReceiver(_configRev);

        super.onDestroy();
    }

    /**
     * 处理“后退”按键，避免误触导致退出应用
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // 仅处理后退键
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            // 避免误操作，不允许通过后退键退出应用
            if (_webView.canGoBack()) {
                _webView.goBack();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 在ActionBar上创建菜单，提供回到首页、刷新页面两个常用功能
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // 依次创建默认的菜单
        menu.add(0, MENU_ID_HOME, 0, getString(R.string.ui_menu_home));
        menu.add(0, MENU_ID_REFRESH, 1, getString(R.string.ui_menu_refresh));
        // menu.add(0, MENU_ID_ABOUT, 3, getString(R.string.ui_menu_about));

        // 你可以在这里创建新的菜单

        return true;
    }

    /**
     * 处理菜单的点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_HOME:
                _webView.loadUrl(getEntryUrl());
                break;
            case MENU_ID_REFRESH:
                _webView.reload();
                break;
            case MENU_ID_ABOUT:
                _webView.loadUrl("file:///android_asset/about/about.html");
                break;

            // 你可以在这里处理新创建菜单的点击事件
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 发起Activity调用请求后，处理收到的响应
     * 主要工作是将响应数据分发到以当前以Activity作为上下文的其他对象中，顺序为：
     * 1. 浏览器内核事件
     * 2. JS桥
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 优先分发给浏览器内核事件
        if (_webChromeClient.ProcessActivityResult(requestCode, resultCode, data)) {
            return;
        }

        // 依次分发给所有JS桥
        for (BaseBridge br :
                _bridges) {
            if (br.ProcessActivityResult(requestCode, resultCode, data)) {
                break;
            }
        }
    }
}