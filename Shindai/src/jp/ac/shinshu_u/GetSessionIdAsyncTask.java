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
		//セッションIDの取得
		String s = GetSessionIdTask.getSessionId(g, p); //aの戻り値＝セッションID
		String hogehoge ="";

		if(s.equals("error")){
			hogehoge = "error";
		}else{
			StringBuilder buf = new StringBuilder();
			StringBuilder kyuukou = new StringBuilder();
			StringBuilder data = new StringBuilder();

			buf.append(LoginStrings.https);
			buf.append(LoginStrings.kyuukou);
			buf.append(LoginStrings.session);
			buf.append(s);

			//部局を選択するための処理
			if(!b.equals("ALL")){
				buf.append(LoginStrings.bukyoku);
				buf.append(b);
			}

			// startがnullでなければ開始日を指定
			if(st != null){
				//取得する日時を指定する処理　開始日
				buf.append(LoginStrings.start);
				buf.append(st);
			}

			// endがnullでなければ終了日を指定
			if(ed != null){
				//部局を選択するための処理
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
			int c = 0;

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

						//表示させた数をカウント
						i++;
						// 今はサンプルなので+にしてるが，文字列の結合はStringBuilder推奨
						//hogehoge = hogehoge + line + "\n\n";
					}else{
						String[] RowData = line.split(",");
						data.append("開講年度");
						data.append(RowData[0]);
						data.append(",");
						data.append("開講部局");
						data.append(RowData[3]);
						data.append("\n");
						data.append("講義名");
						data.append(RowData[6]);
						data.append("\n");
						data.append("開講場所");
						data.append(RowData[11]);
						data.append(",");
						data.append("区分");
						data.append(RowData[14]);
						data.append("\n");
						data.append("休講日");
						data.append(RowData[15]);
						data.append(",");
						data.append("講義場所");
						data.append(RowData[16]);
						data.append(",");
						data.append("時限");
						data.append(RowData[17]);
						data.append("\n");
						data.append("連絡事項");
						data.append(RowData[18]);
						data.append("\n");
						data.append("登録日");
						data.append(RowData[19]);
						data.append(",");
						data.append("更新日");
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

			//表示させていない者がある場合のみ、以下を表示
			if(c != 0){
				kyuukou.append("以上です\n");
				kyuukou.append("残り"+c+"件\n");
				kyuukou.append("残りを確認する場合は設定から表示件数を変更し、再度取得してください");
			}

			// 今はサンプルなので+にしてるが，文字列の結合はStringBuilder推奨
			//return params[0] + hogehoge;
			hogehoge = kyuukou.toString();
		}
		return params[0] + hogehoge;

		// ↓nullが返された時にきちんとSessionIdが取得できるか確認する用
		//return uri;
	};

	@Override
	protected void onPostExecute(String hogehoge) {
		textView.setText(hogehoge);
	};
}
