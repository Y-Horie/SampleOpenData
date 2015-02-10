package jp.ac.shinshu_u;

import jp.ac.shinshu_u.SetConstant.LoginStrings;
import android.app.Activity;

public class loginActivity extends Activity {

	//セッションIDを取得するための文字列の生成
	public static String setSession(String e, String p){

		StringBuilder buf = new StringBuilder();
		String uri;

		//お決まり文句
		buf.append(LoginStrings.https);
		buf.append(LoginStrings.login);
		buf.append(LoginStrings.key);

		//ID=学籍番号
		buf.append(LoginStrings.ID);
		buf.append(e);

		//pass=パスワード
		buf.append(LoginStrings.Pass);
		buf.append(p);

		// 文字列へ変換
		uri = buf.toString();

		return uri;
	}

	public static String setGetNewData(String sessionId, String p_day){
		StringBuilder buf = new StringBuilder();
		String uri;

		// 休講情報に関する文字列
		buf.append(LoginStrings.https);
		buf.append(LoginStrings.kyuukou);
		buf.append(LoginStrings.session);

		// 新しいデータ取得に関する文字列
		buf.append(sessionId);
		buf.append(LoginStrings.and);
		buf.append(LoginStrings.update);
		buf.append(p_day);

		// 文字列へ変換
		uri = buf.toString();

		return uri;
	}

	public static String setGetData(String sessionId){
		StringBuilder buf = new StringBuilder();
		String uri;

		// 休講情報に関する文字列
		buf.append(LoginStrings.https);
		buf.append(LoginStrings.kyuukou);
		buf.append(LoginStrings.session);

		// セッションIDまでを渡す
		buf.append(sessionId);

		// 文字列へ変換
		uri = buf.toString();

		return uri;
	}
}
