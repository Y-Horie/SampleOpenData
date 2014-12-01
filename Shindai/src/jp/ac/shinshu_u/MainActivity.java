/*
 * ◎変更点
 * 新しい機能の実装：
 * アップデートを自動で確認可能に
 * アップデートの自動確認を行うかの選択
 *
 * その他、見えてはいけないところが出ていたので修正
 * IDの取得専用のクラスの作成：GetSessionIdTask.java
 *
 *
 * 基本的にprivate
 * 他のクラスから見たい場合は、get◎◎を作る←これはpublic
 *
 * ◎ギットハブ
 * 注意点：プライベートにならない
 * URLやキーを別ファイルに
 */

package jp.ac.shinshu_u;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import jp.ac.shinshu_u.SetCodelist.CodeList;
import jp.ac.shinshu_u.SetConstant.SystemInteger;
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
	static String param;
	static int day;

	SystemInteger start_s;
	SystemInteger end_s;
	SystemInteger attention;

	String start;
	String end;

	DatePickerDialog datePickerDialog;

	private Button getSessionIdButton;
	private Button test;
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
		ArrayAdapter<CodeList> a1
		= new ArrayAdapter<CodeList>(this, android.R.layout.simple_spinner_item, SetCodelist.CodeList.values());

		// Androidによる選択ダイアログを表示
		a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// スピナーをインスタンス化し、Adapterにセット
		// 部局については書き替えが出来るようにCodelistactivity内で管理
		Spinner s1 = (Spinner) findViewById(R.id.bukyoku);
		s1.setAdapter(a1);

		//更新データの有無を取得するかを取得する
		SharedPreferences pr = PreferenceManager.getDefaultSharedPreferences(this);
		boolean param_AC = pr.getBoolean("autocheck", false); //初期値は5

		//自動取得
		if(param_AC == true ){
			//テスト
			String g = getParam_g();
			String p = getParam_p();
			String p_day = getPrevDay();

			GetNewData newdata = new GetNewData(getApplicationContext(), g, p, p_day);
			newdata.execute();
		}

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
				CodeList b = getItem();
				int n = getParam_n();

				// 日にちの指定がなかった時の処理
				if(start_s == SystemInteger.off){
					start = null;
				}
				if(end_s == SystemInteger.off){
					end = null;
				}

				// 名称をコードへ変換
				String b_ = b.name();

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

		gakuseki.setText("こんにちは" + name + " さん\n"
				+ "前回の起動：" + text_r + "\n\n");
	}

	// ここから（可能であれば）改良したいエリア -------------------------------------------
	// Spinnerの値取得をcodelistActivity内, Preferencesの値取得をloginActivity内で行いたい
	// 値が上手く渡せなかったのでとりあえずMain内で読み込んで対処中
	// 学籍番号
	private String getParam_g(){
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		// 値の取得
		param_g = p.getString("edittext", "Unselected");
		return param_g;
	}

	// パスワード
	private String getParam_p(){
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		param_p = p.getString("loginPass", "Unselected");
		return param_p;
	}

	// 表示数
	private int getParam_n(){
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		param = p.getString("put_num", "5"); //初期値は5
		int num = Integer.parseInt(param);
		return num;
	}

	// 範囲の指定（Spinnerの値取得）　部局
	private CodeList getItem(){
		CodeList item;
		Spinner sp = (Spinner)findViewById(R.id.bukyoku);
		item = (CodeList)sp.getSelectedItem();
		return item;
	}

	private String getPrevDay(){
		//ついでに起動時間の事
		String p_day;
		String str ="";
		try{
			Context ctxt = createPackageContext("jp.ac.shinshu_u",0);
			SharedPreferences pref1 =
					ctxt.getSharedPreferences("data",MODE_PRIVATE);
			str = pref1.getString("data",null);
		}catch (NameNotFoundException e){
			e.printStackTrace();
		}
		p_day = str.toString();
		return p_day;
	}

	// 最新更新日取得用、アプリ起動時間取得
	private void SaveStartTime(){
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
	DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(android.widget.DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
			StringBuffer day = new StringBuffer();

			monthOfYear++;
			day.append(year + "/");
			day.append(monthOfYear + "/");
			day.append(dayOfMonth);

			//日付を格納する変数を場合分け
			switch(attention){
			case a_start:
				start = day.toString();
				//確認用メッセージ
				setday = (TextView)findViewById(R.id.start_st);
				setday.setText(start);
				break;
			case a_end:
				end = day.toString();
				//確認用メッセージ
				setday = (TextView)findViewById(R.id.end_st);
				setday.setText(end);
				break;
			default:
				break;
			}
		}
	};

	// 開始・終了日の取得
	private void setDay(){
		attention = SystemInteger.a_;

		setStartButton = (Button)findViewById(R.id.start_but);
		setStartButton.setText("指定無");
		start_s = SystemInteger.off;

		setEndButton = (Button)findViewById(R.id.end_but);
		setEndButton.setText("指定無");
		end_s = SystemInteger.off;

		// 開始日について
		setStartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//enumでこの辺書き替え可能　http://javatechnology.net/java/enum-compared/
				switch(start_s){
				case off:
					//開始日指定有
					setStartButton.setText("指定有");
					attention = SystemInteger.a_start;
					getDay();
					start_s = SystemInteger.on;
					break;
				case on:
					//開始日指定無
					setStartButton.setText("指定無");
					attention = SystemInteger.a_;
					start_s = SystemInteger.off;

					//確認用メッセージ
					setday = (TextView)findViewById(R.id.start_st);
					setday.setText("");
					break;
				default:
					break;
				}
			}
		});

		// 終了日について
		setEndButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(end_s){
				case off:
					//終了日指定有
					setEndButton.setText("指定有");
					attention = SystemInteger.a_end;
					getDay();
					end_s = SystemInteger.on;
					break;
				case on:
					//終了日指定無
					setEndButton.setText("指定無");
					attention = SystemInteger.a_;
					end_s = SystemInteger.off;

					//確認用メッセージ
					setday = (TextView)findViewById(R.id.end_st);
					setday.setText("");
					break;
				default:
					break;
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
				android.R.style.Theme_Black_NoTitleBar, dateSetListener, year,
				monthOfYear, dayOfMonth);

		// 日付設定ダイアログの表示
		datePickerDialog.show();
	}

	//追加
	/*
	private void setMoji(){
		// データ取得に必要な変数の定義
		String g = getParam_g();
		String p = getParam_p();
		String b = getItem();
		int n = getParam_n();

	}
	 */
}
