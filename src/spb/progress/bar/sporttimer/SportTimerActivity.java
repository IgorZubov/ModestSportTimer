package spb.progress.bar.sporttimer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SportTimerActivity extends Activity {
	public static String ROUNDS_AMOUNT = "repets_amount";
	public static String ROUND_DURATION = "amount_seconds";
	public static String REST_DURATION = "pause_seconds";
	private int m_nRoundsAmout = 0;
	private int m_nRoundInSeconds = 0;
	private int m_nRestInSeconds = 0;
	private Button m_btnGo;
	private EditText m_txtRounds;
	private EditText m_txtRoundDuration;
	private EditText m_txtRestDuration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_timer);
		m_txtRounds = (EditText) findViewById(R.id.edt_repeat_amounts);
		m_txtRounds.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"));
		m_txtRoundDuration = (EditText) findViewById(R.id.edt_repeat_duration);
		m_txtRoundDuration.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"));
		m_txtRestDuration = (EditText) findViewById(R.id.edt_pause_duration);
		m_txtRestDuration.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"));
		
		TextView repeatDuration = (TextView) findViewById(R.id.tv_repeat_duration);
		repeatDuration.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"));
		TextView pauseDuration = (TextView) findViewById(R.id.tv_pause_duration);
		pauseDuration.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"));
		TextView repeatAmounts = (TextView) findViewById(R.id.tv_repeat_amounts);
		repeatAmounts.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"));
		
		m_btnGo = (Button) findViewById(R.id.btn_go);
		m_btnGo.setOnClickListener(new ButtonGoOnClickListener());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		String repeats = m_nRoundsAmout == 0 ? "" : String.valueOf(m_nRoundsAmout);
		String duration = m_nRoundInSeconds == 0 ? "" : String.valueOf(m_nRoundInSeconds / CountdownActivity.MILLISEC_IN_SEC);
		String pause = m_nRestInSeconds == 0 ? "" : String.valueOf(m_nRestInSeconds / CountdownActivity.MILLISEC_IN_SEC);
		if (m_nRoundsAmout != 0) {
			m_txtRounds.setText(repeats);
		}
		if (m_nRoundInSeconds != 0) {
			m_txtRoundDuration.setText(duration);
		}
		if (m_nRestInSeconds != 0) {
			m_txtRestDuration.setText(pause);
		}
	}
	
	class ButtonGoOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			try {
				m_nRoundsAmout = Integer.parseInt(m_txtRounds.getText().toString());
				m_nRoundInSeconds = Integer.parseInt(m_txtRoundDuration.getText().toString()) * CountdownActivity.MILLISEC_IN_SEC;
				m_nRestInSeconds = Integer.parseInt(m_txtRestDuration.getText().toString()) * CountdownActivity.MILLISEC_IN_SEC;
			} catch (NumberFormatException e) {
				m_nRoundsAmout = 0;
				m_nRoundInSeconds = 0;
				m_nRestInSeconds = 0;
			}
			if (m_nRoundsAmout == 0 || m_nRoundInSeconds == 0 || m_nRestInSeconds == 0) {
				Toast.makeText(getApplicationContext(), "Type correct numbers, please", Toast.LENGTH_LONG).show();
			} else {
				Intent countDownIntent = new Intent(getApplicationContext(), CountdownActivity.class);
				countDownIntent.putExtra(ROUNDS_AMOUNT, m_nRoundsAmout);
				countDownIntent.putExtra(ROUND_DURATION, m_nRoundInSeconds);
				countDownIntent.putExtra(REST_DURATION, m_nRestInSeconds);
				startActivity(countDownIntent);
			}
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		try {
			m_nRoundsAmout = Integer.parseInt(m_txtRounds.getText().toString());
			m_nRoundInSeconds = Integer.parseInt(m_txtRoundDuration.getText().toString()) * CountdownActivity.MILLISEC_IN_SEC;
			m_nRestInSeconds = Integer.parseInt(m_txtRestDuration.getText().toString()) * CountdownActivity.MILLISEC_IN_SEC;
		} catch (NumberFormatException e) {
		}
		outState.putInt(ROUNDS_AMOUNT, m_nRoundsAmout);
		outState.putInt(ROUND_DURATION, m_nRoundInSeconds);
		outState.putInt(REST_DURATION, m_nRestInSeconds);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		m_nRoundsAmout = savedInstanceState.getInt(ROUNDS_AMOUNT, 0);
		m_nRoundInSeconds = savedInstanceState.getInt(ROUND_DURATION, 0);
		m_nRestInSeconds = savedInstanceState.getInt(REST_DURATION, 0);
	}
	
}
