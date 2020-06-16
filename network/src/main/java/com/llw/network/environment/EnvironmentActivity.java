package com.llw.network.environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.llw.network.R;
import com.llw.network.utils.ToastUtils;

/**
 * 设置不同网络环境
 */
public class EnvironmentActivity extends AppCompatActivity {

    public static final String NETWORK_ENVIRONMENT = "network_environment";//网络环境
    private static String mCurrentNetworkEnvironment = "";//当前网络环境

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content,new MyPreferenceFragment())
                .commit();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);//获取默认缓存
        mCurrentNetworkEnvironment = preferences.getString(EnvironmentActivity.NETWORK_ENVIRONMENT,"1");//如果没有值就默认为 “1”  在这里 1 表示正式环境
    }

    //这里创建一个内部类继承自PreferenceFragmentCompat并实现一个缓存的变化监听
    public static class MyPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener{
        //创建缓存
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            //这个相当于setContentView，从资源文件中添Preferences ，选择的值将会自动保存到SharePreferences
            addPreferencesFromResource(R.xml.environment_preference);
            //设置缓存变化监听
            findPreference(NETWORK_ENVIRONMENT).setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if(!mCurrentNetworkEnvironment.equalsIgnoreCase(String.valueOf(newValue))){//当前值与缓存中不一致时，说明切换了网络，这时提醒一下
                ToastUtils.ShortToast(getContext(),"您已经更改了网络环境，再您退出当前页面的时候APP将会重启切换环境！");
            }
            return true;
        }
    }

    //页面返回
    @Override
    public void onBackPressed() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String newValue = preferences.getString(EnvironmentActivity.NETWORK_ENVIRONMENT,"1");
        if(!mCurrentNetworkEnvironment.equalsIgnoreCase(newValue)){//不一致.说明有修改
            android.os.Process.killProcess(android.os.Process.myPid());//从操作系统中结束掉当前程序的进程
        }else {//一致  没有修改则关闭当前页面
            finish();
        }
    }

    //是否为正式网络环境
    public static boolean isOfficialEnvironment(Application application) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        String environment = prefs.getString(EnvironmentActivity.NETWORK_ENVIRONMENT, "1");
        return "1".equalsIgnoreCase(environment);
    }
}
