package com.huozige.lab.container;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * 配置相关的帮助类
 */
public class ConfigManager {

    static  final int DEFAULT_ACTIONBAR_COLOR=0x555555;
    static final String PREFERENCE_NAME = "HAC";
    static final String PREFERENCE_KEY_ENTRY = "E"; // 页面入口的地址
    static final String PREFERENCE_KEY_SCAN_ACTION = "SA"; // 扫描广播的Action
    static final String PREFERENCE_KEY_SCAN_EXTRA = "SE"; // 扫描广播的Extra
    static final String PREFERENCE_KEY_HA = "HA"; // 硬件加速
    static final String PREFERENCE_KEY_TCD = "TCD"; // 主题色（暗色）
    static final String PREFERENCE_KEY_MENU_SETTING_V = "MSV"; // 是否显示设置菜单
    static final String PREFERENCE_KEY_ULR_ABOUT = "URLA"; // 关于页面的链接
    static final String PREFERENCE_KEY_ULR_HELP= "URLH"; // 帮助页面的链接

    private final Activity _context;

    public ConfigManager(Activity context){
        _context = context;
    }

    /**
     * 获取应用入口页面
     * @return 默认为空字符串
     */
    public  String GetEntry(){
        return GetStringValue(_context, PREFERENCE_KEY_ENTRY, R.string.app_default_entry);
    }

    /**
     * 获取扫描头的广播名称（Action）
     * @return 默认为com.android.server.scan
     */
    public  String GetScanAction(){
        return GetStringValue(_context, PREFERENCE_KEY_SCAN_ACTION, R.string.feature_scanner_broadcast_name);
    }

    /**
     * 获取广播的键值名称
     * @return 默认为scannerdata
     */
    public  String GetScanExtra(){
        return GetStringValue(_context, PREFERENCE_KEY_SCAN_EXTRA, R.string.feature_scanner_extra_key_barcode_broadcast);
    }

    /**
     * 获取是否开启硬件加速
     * @return 默认为软件加速
     */
    public  Boolean GetHA(){
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 从数据库中加载，默认为软件加速
        return sharedPref.getBoolean(PREFERENCE_KEY_HA, false);
    }

    /**
     * 获取动作栏和状态栏的颜色
     * @return 默认为深灰色
     */
    public  int GetTCD(){
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 从数据库中加载，默认为配置的主题色
        return sharedPref.getInt(PREFERENCE_KEY_TCD, DEFAULT_ACTIONBAR_COLOR);
    }

    public Boolean GetSettingMenuVisible(){
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 从数据库中加载，默认为显示菜单
        return sharedPref.getBoolean(PREFERENCE_KEY_MENU_SETTING_V, true);
    }

    public String GetAboutUrl(){
        return GetStringValue(_context, PREFERENCE_KEY_ULR_ABOUT, R.string.app_default_entry);
    }

    public String GetHelpUrl(){
        return GetStringValue(_context, PREFERENCE_KEY_ULR_HELP, R.string.app_default_entry);
    }

    //==================== 下面是设置

    public  void UpsertSettingMenuVisible(Boolean value){
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 保存到配置库
        sharedPref.edit().putBoolean(PREFERENCE_KEY_MENU_SETTING_V, value).apply();
    }

    public  void UpsertAboutUrl(String value){
        UpsertStringValue(_context,PREFERENCE_KEY_ULR_ABOUT,value);
    }

    public  void UpsertHelpUrl(String value){
        UpsertStringValue(_context,PREFERENCE_KEY_ULR_HELP,value);
    }

    public  void UpsertEntry(String value){
        UpsertStringValue(_context,PREFERENCE_KEY_ENTRY,value);
    }

    public  void UpsertScanAction(String value){
        UpsertStringValue(_context,PREFERENCE_KEY_SCAN_ACTION,value);
    }

    public  void UpsertScanExtra(String value){
        UpsertStringValue(_context,PREFERENCE_KEY_SCAN_EXTRA,value);
    }

    public  void UpsertHA(Boolean enabled){
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 保存到配置库
        sharedPref.edit().putBoolean(PREFERENCE_KEY_HA, enabled).apply();
    }


    public  void UpsertTCD(int colorValue){
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 保存到配置库
        sharedPref.edit().putInt(PREFERENCE_KEY_TCD, colorValue).apply();
    }

    /**
     * 读取配置
     */
    private static String GetStringValue(Activity context, String key, int defaultValueStringId) {
        // 从配置库中读取启动地址
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        return sharedPref.getString(key, context.getString(defaultValueStringId));
    }

    /**
     * 写入配置
     */
    private static void UpsertStringValue(Activity context, String key, String value){
        // 打开配置库
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 保存到配置库
        sharedPref.edit().putString(key, value).apply();
    }

}