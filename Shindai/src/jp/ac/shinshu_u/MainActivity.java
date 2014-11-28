/*
 * ◎ギットハブ
 * 注意点：プライベートにならない
 * URLやキーを別ファイルに
 */

package jp.ac.shinshu_u;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView gakuseki;
	TextView login;
	TextView setday;

	static String param_g;
	static String param_p;

	static String item;
	static String param;
	static int day;

	int start_s;
	int end_s;
	int attention;

	String start;
	String end;

	DatePickerDialog datePickerDialog;

	private Button getSessionIdButton;
	private Button setStartButton;
	private Button setEndButton;

	//変数の定義
	SetCodelist moe = new SetCodelist();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// こんにちは○○さんを表示
		setTxt();
		setDay();

		// 部局の中身の表示
		ArrayAdapter<String> a1
		= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SetCodelist.name);

		// Androidによる選択ダイアログを表示
		a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// スピナーをインスタンス化し、Adapterにセット
		// ※月と日は変わる可能性がほぼないのでスピナーの値はxmlファイル内で管理。
		// 部局については書き替えが出来るようにCodelistactivity内で管理
		Spinner s1 = (Spinner) findViewById(R.id.bukyoku);
		s1.setAdapter(a1);

		// 非同期処理
		login = (TextView) findViewById(R.id.login);

		// CSV取得ボタン
		getSessionIdButton = (Button)findViewById(R.id.get_session_id_button);
		getSessionIdButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((Button)v).setEnabled(false);

				// データ取得に必要な変数の定義
				String g = getParam_g();
				String p = getParam_p();
				String b = getItem();
				int n = getParam_n();

				// 日にちの指定がなかった時の処理
				if(start_s == SetConstant.off){
					start = null;
				}
				if(end_s == SetConstant.off){
					end = null;
				}

				// 名称をコードへ変換
				String b_ = moe.getCode(b);

				//デバッグ用
				//gakuseki = (TextView)findViewById(R.id.gakuseki);
				//gakuseki.setText("st:" + start + "ed:" + end + "\n\n");

				// 最後にデータを取得した時のこと
				SaveStartTime();

				final GetSessionIdAsyncTask getSessionIdAsyncTask =
						new GetSessionIdAsyncTask(login, g, p, b_, n, start, end);

				// 非同期処理を開始する
				getSessionIdAsyncTask.execute("");
			}
		});
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
			this.finish();
			return true;
		case R.id.menu3:
			//各項目についての説明のページへ飛ばす
			this.finish();
			break;
		case R.id.menu4:
			//プログラムの終了
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// ようこそ○○さん
	private void setTxt(){
		//表示領域用の変数
		gakuseki = (TextView)findViewById(R.id.gakuseki);
		String name = getParam_g();

		//ついでに起動時間の事
		String text_r;
		String str ="";
		try{
			Context ctxt = createPackageContext("jp.ac.shinshu_u",0);
			SharedPreferences pref1 =
					ctxt.getSharedPreferences("data2",MODE_PRIVATE);
			str = pref1.getString("data2","初回起動");
		}catch (NameNotFoundException e){
			e.printStackTrace();
		}
		text_r = str.toString();
		gakuseki.setText(text_r);

		gakuseki.setText("こんにちは" + name + " さん\n"
				+ "前回の起動：" + text_r + "\n\n");
	}

	// ここから（可能であれば）改良したいエリア -------------------------------------------
	// Spinnerの値取得をcodelistActivity内, Preferencesの値取得をloginActivity内で行いたい
	// 値が上手く渡せなかったのでとりあえずMain内で読み込んで対処中
	// 学籍番号
	public String getParam_g(){
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		// 値の取得
		param_g = p.getString("edittext", "Unselected");
		return param_g;
	}

	// パスワード
	public String getParam_p(){
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		param_p = p.getString("loginPass", "Unselected");
		return param_p;
	}

	// 表示数
	public int getParam_n(){
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		param = p.getString("put_num", "5"); //初期値は5
		int num = Integer.parseInt(param);
		return num;
	}

	// 範囲の指定（Spinnerの値取得）　部局
	public String getItem(){
		Spinner sp = (Spinner)findViewById(R.id.bukyoku);
		item = (String)sp.getSelectedItem();
		return item;
	}

	// 最新更新日取得用、アプリ起動時間取得
	public void SaveStartTime(){
		Date date1 = new Date();
		// 最新更新のものを取得する用
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",Locale.JAPANESE);
		// 表示用
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy'年'MM'月'dd'日'k'時'",Locale.JAPANESE);

		// プリファレンスの定義
		SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
		SharedPreferences pref2 = getSharedPreferences("data2", MODE_PRIVATE);

		// 書き込むための準備
		Editor edi = pref.edit();
		Editor edi2 = pref2.edit();

		// 書き込むデータをセット
		edi.putString("data", sdf.format(date1));
		edi2.putString("data2", sdf2.format(date1));

		// 書き込む
		edi.commit();
		edi2.commit();
	}

	// 日付設定時のリスナ作成
	DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(android.widget.DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
			StringBuffer day = new StringBuffer();

			monthOfYear++;
			day.append(year + "/");
			day.append(monthOfYear + "/");
			day.append(dayOfMonth);

			if(attention == SetConstant.a_start){
				start = day.toString();
				//確認用メッセージ
				setday = (TextView)findViewById(R.id.start_st);
				setday.setText("開始:" + start);
			}else{
				end = day.toString();
				//確認用メッセージ
				setday = (TextView)findViewById(R.id.end_st);
				setday.setText("終了:" + end);
			}
		}
	};

	// 開始・終了日の取得
	private void setDay(){
		attention = SetConstant.a_;

		setStartButton = (Button)findViewById(R.id.start_but);
		setStartButton.setText("指定無");
		start_s = SetConstant.off;

		setEndButton = (Button)findViewById(R.id.end_but);
		setEndButton.setText("指定無");
		end_s = SetConstant.off;

		// 開始日について
		setStartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(start_s == SetConstant.off){
					//開始日指定有
					setStartButton.setText("指定有");
					attention = SetConstant.a_start;
					getDay();
					start_s = SetConstant.on;
				}else{
					//開始日指定無
					setStartButton.setText("指定無");
					attention = SetConstant.a_;
					start_s = SetConstant.off;
				}
			}
		});

		// 終了日について
		setEndButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(end_s == SetConstant.off){
					//終了日指定有
					setEndButton.setText("指定有");
					attention = SetConstant.a_end;
					getDay();
					end_s = SetConstant.on;
				}else{
					//終了日指定無
					setEndButton.setText("指定無");
					attention = SetConstant.a_;
					end_s = SetConstant.off;
				}
			}
		});
	}

	// カレンダーピッカ
	private void getDay(){
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR); // 年
		int monthOfYear = calendar.get(Calendar.MONTH); // 月
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); // 日

		// 日付設定ダイアログの作成・リスナの登録
		datePickerDialog = new DatePickerDialog(this,
				android.R.style.Theme_Black_NoTitleBar, DateSetListener, year,
				monthOfYear, dayOfMonth);

		// 日付設定ダイアログの表示
		datePickerDialog.show();
	}
}
