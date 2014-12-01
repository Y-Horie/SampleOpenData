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

public class GetSessionIdTask {

	String g;
	String p;
	String sessionId;

	static public String getSessionId(String g, String p){
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
		//return sessionId;
		return sessionId;
	}
}
