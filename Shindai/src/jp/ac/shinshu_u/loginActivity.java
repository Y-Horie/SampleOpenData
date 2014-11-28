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

		uri = buf.toString();

		return uri;
	}

	//セッションIDをくっつける処理（使ってない）
	public static String comSession(String s){

		StringBuilder buf = new StringBuilder();
		SetConstant moe = new SetConstant();
		String uri;

		buf.append(LoginStrings.https);
		//今扱っているのが休講情報のみなので、ここは_kyuukouで固定
		//今後、他のデータも扱えるようになったら適宜変更
		buf.append(LoginStrings.kyuukou);
		buf.append(LoginStrings.session);
		buf.append(s);

		uri = buf.toString();

		return uri;
	}

}
