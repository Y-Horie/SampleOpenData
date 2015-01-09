package jp.ac.shinshu_u;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jp.ac.shinshu_u.SetConstant.LoginStrings;
import jp.ac.shinshu_u.SetConstant.SystemInteger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

public class GetNewData extends AsyncTask<String, String, String>{

	private TextView text;
	private String g;
	private String p;
	private String p_day;
	private String n_day;

	Toast toast;
	String hoge;
	Context context;
	SystemInteger update_s;

	public GetNewData(TextView text, Context context, String g, String p, String p_day, String n_day) {
		super();
		this.text = text;
		this.context = context;
		this.g = g; //学籍番号
		this.p = p; //パスワード
		this.p_day = p_day;
		this.n_day = n_day;
	}

	@Override
	protected String doInBackground(String... params) {
		String sessionId = "error";
		String uri = loginActivity.setSession(g, p);
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(uri);
		HttpResponse response = null;
		BufferedReader br = null;

		try {
			response = httpClient.execute(httpGet, localContext);
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line;
			String condition;
			String hogehoge;

			if ((line = br.readLine()) != null) {
				// カンマで分割するなら下記を参考に実装してください．
				// カラムの数でハードコーディングしない方がいいと思う
				String[] RowData = line.split(",");
				condition = RowData[0]; //SUCSEES or FAIL
				hogehoge = RowData[1];  //sessionId
				if(!condition.equals("\"FAIL\"")){
					// ダブルクオーテーションを削除
					hogehoge = hogehoge.substring(1, hogehoge.length()-1);
					//セッションIDのみを代入
					sessionId = hogehoge;
					// データの取得
					sessionId = getInfo(sessionId);
				}else{
					sessionId += "　学籍番号とパスワードを確認してください";
				}
			}else{
				sessionId = "error";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return params[0] + sessionId;
	};

	private String getInfo(String sessionId){
		//最終確認日が今日かどうか
		//aの戻り値＝セッションID

		StringBuilder buf = new StringBuilder();
		buf.append(LoginStrings.https);
		buf.append(LoginStrings.kyuukou);
		buf.append(LoginStrings.session);
		buf.append(sessionId);

		if(p_day != null){
			buf.append(LoginStrings.update);
			buf.append(p_day);
		}

		//uriを文字列へ
		String uri = buf.toString();

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(uri);
		HttpResponse response = null;
		BufferedReader br = null;

		try {
			response = httpClient.execute(httpGet, localContext);
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "Shift-JIS"));
			String line;
			line = br.readLine(); //一行目は読み飛ばす
			if((line = br.readLine()) != null){
				if(!n_day.equals(p_day)){
					// 今日の日付 != 前の日付なら
					hoge = "追加の情報があります";
					update_s = SystemInteger.on;
				}else{
					// 今日の日付 == 前の日付
					hoge = "追加の情報はありません";
					update_s = SystemInteger.off;
				}
			}else{
				// データの取得が上手く行えなかったら
				hoge = "追加の情報はありません";
				update_s = SystemInteger.off;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return hoge;
	}

	//新着情報の有無の表示
	@Override
	protected void onPostExecute(String sessionId) {
		text.setText(sessionId);
		if(update_s == SystemInteger.on){
			text.setTextColor(Color.RED);
		}
	};
}
