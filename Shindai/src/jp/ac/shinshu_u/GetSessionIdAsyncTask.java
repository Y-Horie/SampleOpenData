package jp.ac.shinshu_u;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jp.ac.shinshu_u.SetConstant.LoginStrings;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

public class GetSessionIdAsyncTask extends AsyncTask<String, String, String> {

	private TextView textView;
	private String g;
	private String p;
	private String b;
	private String st;
	private String ed;
	private int n;
	private ProgressDialog progressDialog;
	private String p_day;
	private boolean ds;
	private Button button;
	private int c = 0;

	public GetSessionIdAsyncTask(TextView textView, String g, String p, String b, int n,
			String st, String ed, ProgressDialog pD, String p_day, boolean data) {
		super();
		this.textView = textView;
		this.g = g; //学籍番号
		this.p = p; //パスワード
		this.b = b; //部局
		this.n = n; //表示数
		this.st = st; //開始日時：yyyy/mm/dd or null
		this.ed = ed; //終了日時：yyyy/mm/dd or null
		this.progressDialog = pD; //グルグルを表示させる
		this.p_day = p_day;
		this.ds = data;
		this.button = button;
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
	}

	// 休講情報を取得
	private String getInfo(String sessionId){
		//セッションIDの取得
		String hogehoge ="";

		StringBuilder buf = new StringBuilder();
		StringBuilder kyuukou = new StringBuilder();
		StringBuilder data = new StringBuilder();

		buf.append(LoginStrings.https);
		buf.append(LoginStrings.kyuukou);
		buf.append(LoginStrings.session);
		buf.append(sessionId);

		// 新着情報のみを取得するか
		if(ds == true){
			buf.append("&");
			buf.append(LoginStrings.update);
			buf.append(p_day);
		}

		//部局を選択するための処理
		if(!b.equals("ALL")){
			buf.append("&");
			buf.append(LoginStrings.bukyoku);
			buf.append(b);
		}

		// startがnullでなければ開始日を指定
		if(st != null){
			//取得する日時を指定する処理　開始日
			buf.append("&");
			buf.append(LoginStrings.start);
			buf.append(st);
		}

		// endがnullでなければ終了日を指定
		if(ed != null){
			//部局を選択するための処理
			buf.append("&");
			buf.append(LoginStrings.end);
			buf.append(ed);
		}

		//uriを文字列へ
		String uri = buf.toString();

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(uri);
		HttpResponse response = null;
		BufferedReader br = null;

		int i = 0;

		try {
			response = httpClient.execute(httpGet, localContext);
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "Shift-JIS"));
			String line;
			line = br.readLine();
			//全件数を調べるためにnullまで回す
			while ((line = br.readLine()) != null) {
				//特定の数で止めるために比較する
				if(i < n){
					// カンマで分割するなら下記を参考に実装してください．
					// カラムの数でハードコーディングしない方がいいと思う
					String[] RowData = line.split(",");
					kyuukou.append(RowData[3]); //開講部局
					kyuukou.append("「");
					kyuukou.append(RowData[6]); //講義名
					kyuukou.append("」");
					kyuukou.append("\n");
					kyuukou.append("休講日：");
					kyuukou.append(RowData[15]);
					kyuukou.append("\n");
					kyuukou.append("講義室：");
					kyuukou.append(RowData[16]);
					kyuukou.append("　");
					kyuukou.append(RowData[17]); //コマ数
					kyuukou.append("時限");
					kyuukou.append("\n");
					kyuukou.append(RowData[0]); //開講年度
					kyuukou.append("年度");
					kyuukou.append(RowData[14]); //開講区分
					kyuukou.append("\n");
					//連絡事項が無ければ読み飛ばす
					if(!RowData[18].equals("\"\"")){
						kyuukou.append("連絡事項：");
						kyuukou.append(RowData[18]);
						kyuukou.append("\n");
					}
					kyuukou.append("更新日：");
					kyuukou.append(RowData[20]);
					kyuukou.append("\n\n");
					i++;
				}else{
					String[] RowData = line.split(",");
					data.append(RowData[3]); //開講部局
					data.append("「");
					data.append(RowData[6]); //講義名
					data.append("」");
					data.append("\n");
					data.append("休講日：");
					data.append(RowData[15]);
					data.append("\n");
					data.append("講義室：");
					data.append(RowData[16]);
					data.append("　");
					data.append(RowData[17]); //コマ数
					data.append("時限");
					data.append("\n");
					data.append(RowData[0]); //開講年度
					data.append("年度");
					data.append(RowData[14]); //開講区分
					data.append("\n");
					//連絡事項が無ければ読み飛ばす
					if(!RowData[18].equals("\"\"")){
						data.append("連絡事項：");
						data.append(RowData[18]);
						data.append("\n");
					}
					data.append("更新日：");
					data.append(RowData[20]);
					data.append("\n\n");
					MainActivity.array.add(data.toString());
					//表示させていない個数のカウント
					c++;
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
		kyuukou.append("以上です\n");
		if(c != 0){
			//表示させていない物がある場合のみ、以下～を表示
			kyuukou.append("残り"+c+"件\n");
		}else{
			// 繰り返して取得された時用の処理
			MainActivity.array.clear();
		}

		// 今はサンプルなので+にしてるが，文字列の結合はStringBuilder推奨
		hogehoge = kyuukou.toString();
		return hogehoge;
	}

	@Override
	protected void onPostExecute(String hogehoge) {
		progressDialog.dismiss(); // グルグルを消す
		textView.setText(hogehoge); //取得したデータn個を表示する
	};
}
