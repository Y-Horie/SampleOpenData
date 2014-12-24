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
		//最終確認日が今日かどうか
		String s = GetSessionIdTask.getSessionId(g, p); //aの戻り値＝セッションID

		StringBuilder buf = new StringBuilder();
		buf.append(LoginStrings.https);
		buf.append(LoginStrings.kyuukou);
		buf.append(LoginStrings.session);
		buf.append(s);

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
				if(n_day.equals(p_day)){
					hoge = "追加の情報があります";
					update_s = SystemInteger.on;
				}else{
					hoge = "追加の情報はありません";
					update_s = SystemInteger.off;
				}
			}else{
				//もし、最終確認日が今日だったら
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
	};

	//新着情報の有無の表示
	@Override
	protected void onPostExecute(String hoge) {
		text.setText(hoge);
		if(update_s == SystemInteger.on){
			text.setTextColor(Color.RED);
		}
		//toast = Toast.makeText(context, hoge, Toast.LENGTH_LONG);
		//toast.show();
	};
}
