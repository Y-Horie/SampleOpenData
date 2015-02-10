package jp.ac.shinshu_u;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

public class GetNewData extends AsyncTask<String, String, String>{

	private TextView text;
	private String g;
	private String p;
	private String p_day;
	private String n_day;

	String hoge;
	Context context;
	SystemInteger update_s; //文字の色の変更に使っている

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
		String uri = loginActivity.setSession(g, p); //学籍番号とパスを含む文字列
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
				// カンマで分ける
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
		// セッションIDが取得出来ていた場合は新着情報に関するメッセージが、
		// 取得できなかった場合はエラーメッセージが 返される
		return params[0] + sessionId;
	};

	private String getInfo(String sessionId){
		//uriを文字列へ
		String uri = loginActivity.setGetNewData(sessionId, p_day);

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
			update_s = SystemInteger.off; //文字の色（デフォルト）

			// 一番最後に取得したのが今日かどうか
			if(n_day.equals(p_day)){
				// 既に同じ日にデータの取得を行っていた場合
				hoge = "本日、すでにデータの取得を行っています";
			}else{
				// 更新されたデータがあるかどうか
				hoge = "追加の情報はありません";
				if((line = br.readLine()) != null){
					// 今日の日付 != 前の日付なら
					hoge = "追加の情報があります";
					update_s = SystemInteger.on; //文字の色（赤）
				}
			}
		// tryの例外処理
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
		// 新しいデータがある時は文字を赤くする
		if(update_s == SystemInteger.on){
			text.setTextColor(Color.RED);
		}
	};
}
