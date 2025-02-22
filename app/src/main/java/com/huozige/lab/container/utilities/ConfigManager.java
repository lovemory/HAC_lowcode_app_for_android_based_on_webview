package com.huozige.lab.container.utilities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.huozige.lab.container.R;

/**
 * 配置相关的帮助类
 */
public class ConfigManager {

    static final String LOG_TAG = "HAC_ConfigManager";

    static final String PREFERENCE_NAME = "HAC";
    static final String PREFERENCE_KEY_ENTRY = "E"; // 页面入口的地址
    static final String PREFERENCE_KEY_SCAN_ACTION = "SA"; // 扫描广播的Action
    static final String PREFERENCE_KEY_SCAN_EXTRA = "SE"; // 扫描广播的Extra
    static final String PREFERENCE_KEY_HA = "HA"; // 硬件加速
    static final String PREFERENCE_KEY_TCD = "TCD"; // 主题色（暗色）
    static final String PREFERENCE_KEY_ACTIONBAR_V = "ABV"; // 是否显示动作栏
    static final String PREFERENCE_KEY_MENU_SETTING_V = "MSV"; // 是否显示设置菜单
    static final String PREFERENCE_KEY_ULR_ABOUT = "URLA"; // 关于页面的链接
    static final String PREFERENCE_KEY_ULR_HELP = "URLH"; // 帮助页面的链接

    static final String PREFERENCE_KEY_USER = "USRN"; // 用户名

    static final String PREFERENCE_KEY_PWD = "PWD"; // 用户密码

    static final String PREFERENCE_KEY_BYPASS_COMPATIBLE_CHECK = "BCC"; // 是否忽略兼容性检查

    final Activity _context;

    public ConfigManager(Activity context) {
        _context = context;
    }

    public Boolean quickConfig(String json) {
        try {
            JSONObject config = (JSONObject) JSONObject.parse(json);

            if (config != null) {

                // 保存配置
                this.upsertEntry(config.getString(PREFERENCE_KEY_ENTRY));
                this.upsertScanAction(config.getString(PREFERENCE_KEY_SCAN_ACTION));
                this.upsertScanExtra(config.getString(PREFERENCE_KEY_SCAN_EXTRA));
                this.upsertAboutUrl(config.getString(PREFERENCE_KEY_ULR_ABOUT));
                this.upsertHelpUrl(config.getString(PREFERENCE_KEY_ULR_HELP));
                this.upsertSettingMenuVisible(config.getString(PREFERENCE_KEY_MENU_SETTING_V).equalsIgnoreCase("1") || config.getString(PREFERENCE_KEY_MENU_SETTING_V).equalsIgnoreCase("true") || config.getString(PREFERENCE_KEY_MENU_SETTING_V).equalsIgnoreCase("yes"));
                this.upsertActionBarVisible(config.getString(PREFERENCE_KEY_ACTIONBAR_V).equalsIgnoreCase("1") || config.getString(PREFERENCE_KEY_ACTIONBAR_V).equalsIgnoreCase("true") || config.getString(PREFERENCE_KEY_ACTIONBAR_V).equalsIgnoreCase("yes"));
                this.upsertTCD(parseHexInteger(config.getString(PREFERENCE_KEY_TCD)));
                this.upsertHA(config.getString(PREFERENCE_KEY_HA).equalsIgnoreCase("1") || config.getString(PREFERENCE_KEY_HA).equalsIgnoreCase("true") || config.getString(PREFERENCE_KEY_HA).equalsIgnoreCase("yes"));
                this.upsertBypassCompatibleCheck(config.getString(PREFERENCE_KEY_BYPASS_COMPATIBLE_CHECK).equalsIgnoreCase("1") || config.getString(PREFERENCE_KEY_BYPASS_COMPATIBLE_CHECK).equalsIgnoreCase("true") || config.getString(PREFERENCE_KEY_BYPASS_COMPATIBLE_CHECK).equalsIgnoreCase("yes"));
                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {
            Log.e(LOG_TAG, String.valueOf(ex));
            return false;
        }
    }

    /**
     * 获取应用入口页面
     *
     * @return 默认为空字符串
     */
    public String getEntry() {
        return getStringValue(_context, PREFERENCE_KEY_ENTRY, R.string.app_default_entry);
    }

    /**
     * 获取扫描头的广播名称（Action）
     *
     * @return 默认为com.android.server.scan
     */
    public String getScanAction() {
        return getStringValue(_context, PREFERENCE_KEY_SCAN_ACTION, R.string.feature_scanner_broadcast_name);
    }

    /**
     * 获取广播的键值名称
     *
     * @return 默认为scannerdata
     */
    public String getScanExtra() {
        return getStringValue(_context, PREFERENCE_KEY_SCAN_EXTRA, R.string.feature_scanner_extra_key_barcode_broadcast);
    }

    /**
     * 获取是否开启硬件加速
     *
     * @return 默认为软件加速
     */
    public Boolean getHA() {
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 从数据库中加载，默认为软件加速
        return sharedPref.getBoolean(PREFERENCE_KEY_HA, false);
    }

    /**
     * 获取动作栏和状态栏的颜色
     *
     * @return 默认为深灰色
     */
    public int getTCD() {
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        int defaultColor = parseHexInteger(_context.getString(R.string.app_customize_action_bar_color));

        // 从数据库中加载，默认为配置的主题色
        return sharedPref.getInt(PREFERENCE_KEY_TCD, defaultColor);
    }

    public Boolean getSettingMenuVisible() {
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 配置文件中的默认值
        boolean defaultVisible = Boolean.parseBoolean(_context.getString(R.string.app_customize_should_show_setting_menu));

        // 从数据库中加载
        return sharedPref.getBoolean(PREFERENCE_KEY_MENU_SETTING_V, defaultVisible);
    }

    public Boolean getActionBarVisible() {
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 配置文件中的默认值
        boolean defaultVisible = Boolean.parseBoolean(_context.getString(R.string.app_customize_should_show_action_bar));

        // 从数据库中加载
        return sharedPref.getBoolean(PREFERENCE_KEY_ACTIONBAR_V,defaultVisible );
    }

    public Boolean getBypassCompatibleCheck() {
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 配置文件中的默认值
        boolean defaultVisible = Boolean.parseBoolean(_context.getString(R.string.app_customize_should_bypass_compatible_check));

        // 从数据库中加载
        return sharedPref.getBoolean(PREFERENCE_KEY_BYPASS_COMPATIBLE_CHECK,defaultVisible );
    }

    public String getAboutUrl() {
        return getStringValue(_context, PREFERENCE_KEY_ULR_ABOUT, R.string.app_customize_url_for_about_menu);
    }

    public String getHelpUrl() {
        return getStringValue(_context, PREFERENCE_KEY_ULR_HELP, R.string.app_customize_url_for_help_menu);
    }

    public String getUserName() {
        return getStringValue(_context, PREFERENCE_KEY_USER, R.string.app_customize_url_for_help_menu);
    }

    public String getPassword() {
        return getStringValue(_context, PREFERENCE_KEY_PWD, R.string.app_customize_url_for_help_menu);
    }

    //==================== 下面是设置

    public void upsertActionBarVisible(Boolean value) {
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 保存到配置库
        sharedPref.edit().putBoolean(PREFERENCE_KEY_ACTIONBAR_V, value).apply();
    }

    public void upsertSettingMenuVisible(Boolean value) {
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 保存到配置库
        sharedPref.edit().putBoolean(PREFERENCE_KEY_MENU_SETTING_V, value).apply();
    }

    public void upsertBypassCompatibleCheck(Boolean value) {
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 保存到配置库
        sharedPref.edit().putBoolean(PREFERENCE_KEY_BYPASS_COMPATIBLE_CHECK, value).apply();
    }

    public void upsertAboutUrl(String value) {
        upsertStringValue(_context, PREFERENCE_KEY_ULR_ABOUT, value);
    }

    public void upsertHelpUrl(String value) {
        upsertStringValue(_context, PREFERENCE_KEY_ULR_HELP, value);
    }

    public void upsertUserName(String value) {
        upsertStringValue(_context, PREFERENCE_KEY_USER, value);
    }

    public void upsertPassword(String value) {
        upsertStringValue(_context, PREFERENCE_KEY_PWD, value);
    }

    public void upsertEntry(String value) {
        upsertStringValue(_context, PREFERENCE_KEY_ENTRY, value);
    }

    public void upsertScanAction(String value) {
        upsertStringValue(_context, PREFERENCE_KEY_SCAN_ACTION, value);
    }

    public void upsertScanExtra(String value) {
        upsertStringValue(_context, PREFERENCE_KEY_SCAN_EXTRA, value);
    }

    public void upsertHA(Boolean enabled) {
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 保存到配置库
        sharedPref.edit().putBoolean(PREFERENCE_KEY_HA, enabled).apply();
    }


    public void upsertTCD(int colorValue) {
        // 打开配置库
        SharedPreferences sharedPref = _context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 保存到配置库
        sharedPref.edit().putInt(PREFERENCE_KEY_TCD, colorValue).apply();
    }

    /**
     * 读取配置
     */
    private static String getStringValue(Activity context, String key, int defaultValueStringId) {
        // 从配置库中读取启动地址
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        return sharedPref.getString(key, context.getString(defaultValueStringId));
    }

    /**
     * 写入配置
     */
    private static void upsertStringValue(Activity context, String key, String value) {
        // 打开配置库
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 保存到配置库
        sharedPref.edit().putString(key, value).apply();
    }

    private int parseHexInteger(String tcd){

        tcd = tcd.replace("#", "");
        tcd = tcd.replace("0x", "");

       return Integer.parseInt(tcd, 16) + 0xFF000000;
    }
}