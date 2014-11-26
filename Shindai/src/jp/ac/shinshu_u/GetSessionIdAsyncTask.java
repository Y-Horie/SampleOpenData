package jp.ac.shinshu_u;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.widget.TextView;



public class GetSessionIdAsyncTask extends AsyncTask<String, String, String> {

	private TextView textView;
	private String g;
	private String p;
	private String b;
	private String st;
	private String ed;
	private int n;

	public GetSessionIdAsyncTask(TextView textView, String g, String p, String b, int n, String st, String ed) {
		super();
		this.textView = textView;
		this.g = g; //学籍番号
		this.p = p; //パスワード
		this.b = b; //部局
		this.n = n; //表示数
		this.st = st; //開始日時：yyyy/mm/dd or null
		this.ed = ed; //終了日時：yyyy/mm/dd or null
	}

	@Override
	protected String doInBackground(String... params) {
		String url = "https://campus-2.shinshu-u.ac.jp/OpenData/OPTest.dll/kyuukou?SessionID=";
		String s = a(); //aの戻り値＝セッションID

		StringBuilder buf = new StringBuilder();
		StringBuilder kyuukou = new StringBuilder();
		buf.append(url);
		buf.append(s);

		//部局を選択するための処理
		if(!b.equals("ALL")){
			String bukyoku = "&bukyoku=";
			buf.append(bukyoku);
			buf.append(b);
		}

		// startがnullでなければ開始日を指定
		if(st != null){
			//取得する日時を指定する処理　開始日
			String ky_start = "&start=";
			buf.append(ky_start);
			buf.append(st);
		}

		// endがnullでなければ終了日を指定
		if(ed != null){
			//部局を選択するための処理
			String ky_end = "&end=";
			buf.append(ky_end);
			buf.append(ed);
		}

		//uriを文字列へ
		String uri = buf.toString();

		//String uri = "https://campus-2.shinshu-u.ac.jp/OpenData/OPTest.dll/login";
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(uri);
		HttpResponse response = null;
		BufferedReader br = null;

		String hogehoge ="";

		try {
			response = httpClient.execute(httpGet, localContext);
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "Shift-JIS"));
			String line;
			line = br.readLine();
			//while ((line = br.readLine()) != null) {
			for (int i = 0; i < n; i++) {
				if((line = br.readLine()) != null){

					// カンマで分割するなら下記を参考に実装してください．
					// カラムの数でハードコーディングしない方がいいと思う
					String[] RowData = line.split(",");
					kyuukou.append("開講年度");
					kyuukou.append(RowData[0]);
					kyuukou.append(",");
					kyuukou.append("開講部局");
					kyuukou.append(RowData[3]);
					kyuukou.append("\n");
					kyuukou.append("講義名");
					kyuukou.append(RowData[6]);
					kyuukou.append("\n");
					kyuukou.append("開講場所");
					kyuukou.append(RowData[11]);
					kyuukou.append(",");
					kyuukou.append("区分");
					kyuukou.append(RowData[14]);
					kyuukou.append("\n");
					kyuukou.append("休講日");
					kyuukou.append(RowData[15]);
					kyuukou.append(",");
					kyuukou.append("講義場所");
					kyuukou.append(RowData[16]);
					kyuukou.append(",");
					kyuukou.append("時限");
					kyuukou.append(RowData[17]);
					kyuukou.append("\n");
					kyuukou.append("連絡事項");
					kyuukou.append(RowData[18]);
					kyuukou.append("\n");
					kyuukou.append("登録日");
					kyuukou.append(RowData[19]);
					kyuukou.append(",");
					kyuukou.append("更新日");
					kyuukou.append(RowData[20]);
					kyuukou.append("\n\n");

					// 今はサンプルなので+にしてるが，文字列の結合はStringBuilder推奨
					//hogehoge = hogehoge + line + "\n\n";
				}else{
					kyuukou.append("以上です\n\n");
					//hogehoge = hogehoge + "以上です" + "\n\n";
					break;
				}
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
		// 今はサンプルなので+にしてるが，文字列の結合はStringBuilder推奨
		//return params[0] + hogehoge;
		//return params[0] + kyuukou.toString();

		// ↓nullが返された時にきちんとSessionIdが取得できるか確認する用
		 return uri;
	};

	@Override
	protected void onPostExecute(String hogehoge) {
		textView.setText(hogehoge);
	};

	//セッションIDの取得
	private String a(){
		String sessionId = "";
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
			String hogehoge;
			while ((line = br.readLine()) != null) {
				// カンマで分割するなら下記を参考に実装してください．
				// カラムの数でハードコーディングしない方がいいと思う
				String[] RowData = line.split(",");
				//hoge = RowData[0];
				hogehoge = RowData[1];

				//グッバイ""
				hogehoge = hogehoge.substring(1, hogehoge.length()-1);

				// 今はサンプルなので+にしてるが，文字列の結合はStringBuilder推奨
				sessionId = sessionId + hogehoge;
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
		// 今はサンプルなので+にしてるが，文字列の結合はStringBuilder推奨
		return sessionId;
	}
}
