package jp.ac.shinshu_u;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 *
 * @author horie
 * ヘルプ画面
 * 現在の内容は簡易版
 *
 */
public class Help extends Activity{

	private TextView text;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		StringBuilder help = new StringBuilder();
		help.append("アプリの使い方(簡易版)\n");
		help.append("アプリを初めて使う際は、ACSUのログインIDとパスワードを設定画面から登録してください。\n");
		help.append("※必要に応じて表示数や、新着情報取得等の設定も行ってください。\n");
		help.append("\n");
		help.append("メイン画面の使い方\n");
		help.append("開始・終了日\n");
		help.append("休講日の範囲の指定が行えます。\n1月1日～1月10日の間に休講する講義について取得する場合は、\n");
		help.append("開始日を1月1日、終了日を1月10日と指定してください。\n");
		help.append("\n");
		help.append("データ取得時の項目の省略\n");
		help.append("部局を省略した場合は、全部局のデータを取得します。\n");
		help.append("開始日を省略した場合は、データ取得日（今日）になります。\n");
		help.append("終了日を省略した場合は、データ取得日（今日）から1週間後になります。\n");
		help.append("1月1日にすべての項目を省略してデータの取得を行った場合は、");
		help.append( "休講日が1月1日～1月8日の全部局のデータが表示されます。\n");

		text = (TextView)findViewById(R.id.help);
		text.setText(help.toString());

		Button button = (Button) findViewById(R.id.button);
		button.setText("戻 る");
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ホーム画面へ戻る
				Intent intent = new Intent();
				intent.setClassName("jp.ac.shinshu_u", "jp.ac.shinshu_u.MainActivity");
				startActivity(intent);
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
			intent.setClassName("jp.ac.shinshu_u", "jp.ac.shinshu_u.MainActivity");
			startActivity(intent);
			this.finish();
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
}
