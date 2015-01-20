package jp.ac.shinshu_u;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SettingActivity extends PreferenceActivity implements OnPreferenceChangeListener{

	// Preference
	private ListPreference list;
	private EditTextPreference edittext;
	private CheckBoxPreference checkbox;
	private CheckBoxPreference AutoCheck;
	private CheckBoxPreference shinchaku;

	private Button button;

	@Override
	public void onCreate(Bundle bundle) {

		super.onCreate(bundle);

		setContentView(R.layout.loginface);
		addPreferencesFromResource(R.xml.login); //バージョンで分ける

		button = (Button)findViewById(R.id.button);
		button.setText("設 定 完 了");

		// Preferenceの取得
		list = (ListPreference)findPreference("put_num");
		edittext = (EditTextPreference)findPreference("edittext");
		checkbox = (CheckBoxPreference)findPreference("savePass");
		AutoCheck = (CheckBoxPreference)findPreference("autocheck");
		shinchaku = (CheckBoxPreference)findPreference("shinchaku");


		// リスナーを設定する
		list.setOnPreferenceChangeListener(this);
		edittext.setOnPreferenceChangeListener(this);
		checkbox.setOnPreferenceChangeListener(this);
		edittext.setOnPreferenceChangeListener(this);
		AutoCheck.setOnPreferenceChangeListener(this);

		// 保存されたデータを読み込む
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);

		// 値の取得
		String param_list = p.getString("put_num", "5");
		String param_edittext = p.getString("edittext", "Unselected");
		boolean param_checkbox = p.getBoolean("savePass", false);
		boolean param_AutoCheck = p.getBoolean("autocheck", false);
		boolean param_shinchaku = p.getBoolean("shinchaku", true);

		// デフォルト値の設定
		list.setDefaultValue(param_list);
		edittext.setDefaultValue(param_edittext);
		checkbox.setDefaultValue(param_checkbox);
		AutoCheck.setDefaultValue(param_AutoCheck);
		AutoCheck.setDefaultValue(param_shinchaku);

		// サマリーの設定
		setSummary(list, param_list);
		setSummary(edittext, param_edittext);
		setSummary(checkbox, param_checkbox);
		setSummary(AutoCheck, param_AutoCheck);

		Button button = (Button) findViewById(R.id.button);

		// ボタン押下時の処理
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent intent = new Intent();
					intent.setClassName("jp.ac.shinshu_u", "jp.ac.shinshu_u.MainActivity");
					startActivity(intent);
					finish();
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							"ERROR\n" + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	// リストの値が変更されたときに呼ばれる
	public boolean onPreferenceChange(android.preference.Preference preference,
			Object newValue) {

		if(newValue != null){

			// newValueの型でサマリーの設定を分ける
			if(newValue instanceof String){

				// preferenceの型でサマリーの設定を分ける
				if(preference instanceof ListPreference)
					setSummary((ListPreference)preference, (String)newValue);
				else if(preference instanceof EditTextPreference)
					setSummary((EditTextPreference)preference, (String)newValue);
			}else if(newValue instanceof Boolean){
				setSummary((CheckBoxPreference)preference, (Boolean)newValue);
			}
			return true;
		}
		return false;
	}

	// Summaryを設定（リスト）
	public void setSummary(ListPreference lp, String param){

		if(param == null){
			lp.setSummary("初期値は5です");
		}else{
			lp.setSummary("Selected「" + param + "」");
		}
		param = null;
	}

	// Summaryを設定（エディットテキスト）
	private void setSummary(EditTextPreference ep, String param) {
		if(param == null){
			ep.setSummary("Unselected");
		}else{
			ep.setSummary(param);
		}
		param = null;
	}

	// Summaryを設定（チェックボックス）
	public void setSummary(CheckBoxPreference cp, boolean param){
		if(param){
			cp.setSummary("する");
		}else{
			cp.setSummary("しない");
		}
		param = false;
	}

	// Activity破棄時に実行
	public void onDestroy(){
		super.onDestroy();
		list.setOnPreferenceChangeListener(null);
		edittext.setOnPreferenceChangeListener(null);
		checkbox.setOnPreferenceChangeListener(null);
		AutoCheck.setOnPreferenceChangeListener(null);
		shinchaku.setOnPreferenceChangeListener(null);

		list = null;
		edittext = null;
		checkbox = null;
		AutoCheck = null;
		shinchaku = null;
	}

	// Activityの再開時に実行
	public void onRestart(){
		super.onRestart();
		list.setEnabled(true);
		edittext.setEnabled(true);
		checkbox.setEnabled(true);
		AutoCheck.setEnabled(true);
		shinchaku.setEnabled(true);
	}

	// Activityの停止時に実行
	public void onStop(){
		super.onStop();
		list.setEnabled(false);
		edittext.setEnabled(false);
		checkbox.setEnabled(false);
		AutoCheck.setEnabled(false);
		shinchaku.setEnabled(false);
	}

	// メニュー作成
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu, menu);

		return true;
	}

	// メニューアイテム選択イベント
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();

		switch (item.getItemId()) {
		case R.id.menu1:
			// メニュー１選択時の処理（更新）
			intent.setClassName("jp.ac.shinshu_u", "jp.ac.shinshu_u.MainActivity");
			startActivity(intent);
			this.finish();
			break;
		case R.id.menu2:
			// メニュー２選択時の処理（設定）
			intent.setClassName("jp.ac.shinshu_u", "jp.ac.shinshu_u.SettingActivity");
			startActivity(intent);
			return true;
		case R.id.menu3:
			//各項目についての説明のページへ飛ばす
			intent.setClassName("jp.ac.shinshu_u", "jp.ac.shinshu_u.Help");
			startActivity(intent);
			break;
		case R.id.menu4:
			//プログラムの終了
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
