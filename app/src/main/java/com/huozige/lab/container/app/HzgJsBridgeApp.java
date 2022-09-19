package com.huozige.lab.container.app;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.huozige.lab.container.HACBaseActivity;
import com.huozige.lab.container.BaseBridge;
import com.huozige.lab.container.ConfigManager;
import com.huozige.lab.container.HzgWebInteropHelpers;

/**
 * 让页面能对APP壳子进行操作
 * app.getVersion(cell)：获取版本号
 * app.getPackageName(cell)：获取入口的包名
 * app.getActionBarColor(cell)：获取标题栏颜色
 * app.setActionBarColor(colorHex)：设置标题栏颜色
 */
public class HzgJsBridgeApp extends BaseBridge {

    String _versionCell, _packageCell; // 单元格位置缓存

    ConfigManager _cm;

    static final String LOG_TAG = "HzgJsBridgeApp";

    /**
     * 基础的构造函数
     *
     * @param context 上下文
     * @param webView 浏览器内核
     */
    public HzgJsBridgeApp(HACBaseActivity context, WebView webView) {
        super(context, webView);

        _cm = new ConfigManager(context);
    }

    /**
     * 注册的名称为：app
     */
    @Override
    public String GetName() {
        return "app";
    }

    /**
     * 无需操作
     */
    @Override
    public void OnActivityCreated() {

    }

    /**
     * 无需操作
     */
    @Override
    public void BeforeActivityDestroy() {

    }

    /**
     * 无需操作
     */
    @Override
    public void BeforeActivityPause() {

    }

    /**
     * 无需操作
     */
    @Override
    public void OnActivityResumed() {

    }

    /**
     * 无需操作
     *
     * @param requestCode 同onActivityResult
     * @param resultCode  同onActivityResult
     * @param data        同onActivityResult
     * @return 跳过这个JS桥，处理下一个
     */
    @Override
    public Boolean ProcessActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    /**
     * 注册到页面的app.getActionBarColor(cell)方法
     * 获取APP ActionBar的颜色
     * 返回的数值是16进制，去掉透明度
     */
    @JavascriptInterface
    public void getActionBarColor(String cell) {

        // 记录参数
        _packageCell = cell;
        int woTrans = _cm.GetTCD() - 0xFF000000;
        HzgWebInteropHelpers.WriteStringValueIntoCell(CurrentWebView, _packageCell,  Integer.toHexString(woTrans));
    }

    /**
     * 注册到页面的app.setActionBarColor(colorInteger)方法
     * 设置APP ActionBar的颜色
     * 输入的颜色是16进制，不需要透明度
     */
    @JavascriptInterface
    public void setActionBarColor(String colorInteger) {

        // 更新配置项
        _cm.UpsertTCD(Integer.parseInt(colorInteger,16)+0xFF000000);

        // 刷新ActionBar
       ActivityContext.refreshActionBarsColor();
    }

    /**
     * 注册到页面的app.getPackageName(cell)方法
     * 获取APP入口的包名
     */
    @JavascriptInterface
    public void getPackageName(String cell) {

        // 记录参数
        _packageCell = cell;
        HzgWebInteropHelpers.WriteStringValueIntoCell(CurrentWebView, _packageCell, ActivityContext.getPackageName());
    }

    /**
     * 注册到页面的app.getVersion(cell)方法
     * 获取版本号
     */
    @JavascriptInterface
    public void getVersion(String cell) {

        // 记录参数
        _versionCell = cell;

        String versionName = "";

        try {
            PackageInfo pinfo = ActivityContext.getPackageManager().getPackageInfo(ActivityContext.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionName = pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "获取应用版本信息出错：" + e);
            e.printStackTrace();
        }

        // 需要调度回主线程操作
        String finalVersionName = versionName;
        HzgWebInteropHelpers.WriteStringValueIntoCell(CurrentWebView, _versionCell, finalVersionName);
    }
}
