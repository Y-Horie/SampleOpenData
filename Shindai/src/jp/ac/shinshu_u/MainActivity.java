/*
 * ◎変更点
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import jp.ac.shinshu_u.SetCodelist.CodeList;
import jp.ac.shinshu_u.SetConstant.SystemInteger;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
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

	private String param_g;
	private String param_p;
	private String param;
	private String g;
	private String p;
	private int n;
	private int putCount;

	SystemInteger start_s;
	SystemInteger end_s;
	SystemInteger attention;
	SystemInteger ATcondition;

	String start;
	String end;

	DatePickerDialog datePickerDialog;

	private Button getSessionIdButton;
	private Button setStartButton;
	private Button setEndButton;

	int i = 0;
	private int sMonth = 0;
	private int eMonth = 0;
	private int sDay = 0;
	private int eDay = 0;
	private int sYear = 0;
	private int eYear = 0;
	private ProgressDialog progressDialog;

	static ArrayList<String> array = new ArrayList<String>();

	//変数の定義
	SetCodelist moe = new SetCodelist();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// こんにちは○○さんを表示
		ATcondition = SystemInteger.off; //ボタンを押したかどうか
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
		boolean param_AC = pr.getBoolean("autocheck", false); //初期値は「確認しない」

		//学籍番号とパスワードを取得
		g = getParam_g();
		p = getParam_p();
		// 一回で表示するデータの個数
		n = getParam_n();

		//処理の状態を明示する
		progressDialog = new ProgressDialog(this);

		//自動取得
		if(param_AC == true &&
				(!g.equals("Unselected")) || (!p.equals("Unselected"))){
			TextView newdata_text = (TextView)findViewById(R.id.text5);
			String p_day = getPrevDay();
			Date date1 = new Date();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",Locale.JAPANESE);
			String n_day = sdf.format(date1).toString();

			newdata_text.setText("更新データの有無を取得中...");

			GetNewData newdata = new GetNewData(newdata_text, getApplicationContext(), g, p, p_day, n_day);
			newdata.execute("");
		}

		// 非同期処理
		login = (TextView) findViewById(R.id.login);

		// CSV取得ボタン
		getSessionIdButton = (Button)findViewById(R.id.get_session_id_button);
		getSessionIdButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// 初めてボタンを押した時
				if(ATcondition == SystemInteger.off){
					// ボタンの文字を変更
					getSessionIdButton.setText("続きを表示");

					//学籍番号とパスワードを取得
					g = getParam_g();
					p = getParam_p();
					// 非同期処理中であることを示すダイアログの表示
					// 非表示にするのは非同期処理で行う
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setMessage("しばらくお待ちください");
					progressDialog.setCancelable(true);
					progressDialog.show();


					// データ取得に必要な変数の定義
					CodeList b = getItem();

					// 日にちの指定がなかった時の処理
					if(start_s == SystemInteger.off){
						start = null;
					}
					if(end_s == SystemInteger.off){
						end = null;
					}

					// 名称をコードへ変換
					String b_ = b.name();

					// 最後にデータを取得した時のこと
					SaveStartTime();

					// データ取得用のインスタンスを生成
					GetSessionIdAsyncTask getSessionIdAsyncTask =
							new GetSessionIdAsyncTask(login, g, p, b_, n,
									start, end, progressDialog, getPrevDay(), getStateData());

					// 非同期処理を開始する
					getSessionIdAsyncTask.execute("");

					// データを取得後、非同期処理用インスタンスを破棄（再取得の為）
					getSessionIdAsyncTask = null;

					ATcondition = SystemInteger.on;
					putCount = 0;

				// 取得したデータがある場合
				}else{
					// 取得したデータに続きがある場合
					if(array.size() != 0){
						for(int j =  0; putCount < array.size(); j++){
							if(j < n){
								login.setText(array.get(putCount));
								putCount++;
								progressDialog.dismiss();
							}else{
								login.append("以上です。残り"+(array.size()-putCount)+"件");
								break;
							}
						}
					}
					//全てのデータを表示し終えた後の処理
					if(array.size() == 0){
						getSessionIdButton.setVisibility(View.INVISIBLE);
					}
					if(putCount >= array.size()){
						getSessionIdButton.setVisibility(View.INVISIBLE);
						login.append("以上です");
					}
				}
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
			array.clear(); //前に取得したデータの削除
			intent.setClassName("jp.ac.shinshu_u", "jp.ac.shinshu_u.MainActivity");
			startActivity(intent);
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

	// ようこそ画面について
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

		// こんにちは○○さんを表示
		gakuseki.setText("こんにちは" + name + " さん\n"
				+ "前回の起動：" + text_r + "\n\n");
	}

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

	// 最新情報があるかを取得するか否か
	private boolean getStateData(){
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		boolean paramData = p.getBoolean("shinchaku", true); //初期値は「最新のデータのみ」
		return paramData;
	}

	// 部局
	private CodeList getItem(){
		CodeList item;
		Spinner sp = (Spinner)findViewById(R.id.bukyoku);
		item = (CodeList)sp.getSelectedItem();
		return item;
	}

	// 前回のデータ取得日時を取得
	private String getPrevDay(){
		String p_day;
		String str ="";
		try{
			// データの読み込み
			Context ctxt = createPackageContext("jp.ac.shinshu_u",0);
			SharedPreferences pref1 =
					ctxt.getSharedPreferences("data",MODE_PRIVATE);
			str = pref1.getString("data",null);
		}catch (NameNotFoundException e){
			e.printStackTrace();
		}
		// 日時データを文字列に
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
			day.append(changeDay(monthOfYear) + "/");
			day.append(changeDay(dayOfMonth));

			//日付を格納する変数を場合分け
			switch(attention){
			// 開始日に関する処理
			case a_start:
				// 取得した日付の格納
				sYear = year;
				sMonth = monthOfYear;
				sDay = dayOfMonth;
				setday = (TextView)findViewById(R.id.start_st);
				start = day.toString();

				// 適切かの判定
				if(eYear != 0){
					checkDay(start, sMonth, eMonth, sDay, eDay, sYear, eYear, setday);
				}else{
					setday.setTextColor(Color.GRAY);
					setday.setText(start);
				}
				break;
			// 終了日に関する処理
			case a_end:
				// 取得した日付の格納
				eYear = year;
				eMonth = monthOfYear;
				eDay = dayOfMonth;
				setday = (TextView)findViewById(R.id.end_st);
				end = day.toString();

				// 適切かの判定
				if(sYear != 0){
					checkDay(end, sMonth, eMonth, sDay, eDay, sYear, eYear, setday);
				}else{
					setday.setTextColor(Color.GRAY);
					setday.setText(end);
				}
				break;
			default:
				break;
			}
		}
	};

	// 入力された日付に関してのチェック
	private void checkDay(String day, int sMonth, int eMonth, int sDay, int eDay,
			int sYear, int eYear, TextView text){
		// 初期値
		text.setTextColor(Color.GRAY);
		text.setText(day);

		// 年をまたぐかどうか
		if(sYear == eYear){
			// 月をまたぐかどうか
			if(sMonth == eMonth){
				if(sDay > eDay){
					text.setTextColor(Color.RED);
					text.setText(day + "\n不適切です");
				}
			}else if(sMonth > eMonth){
				text.setTextColor(Color.RED);
				text.setText(day + "\n不適切です");
			}
		}else if(sYear > eYear){
			text.setTextColor(Color.RED);
			text.setText(day + "\n不適切です");
		}
	}

	//1桁の月日を2桁に変換する
	private String changeDay(int n){
		if(n < 10){
			return "0" + n;
		}else{
			return String.valueOf(n);
		}
	}

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
}
