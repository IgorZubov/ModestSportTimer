package spb.progress.bar.sporttimer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class CountdownActivity extends Activity {
	public static final int MILLISEC_IN_SEC = 1000;
	public static final int DEFAULT_BEEP_TIME_BEFORE_END = 2000;
	public static final int DEFAULT_BEEP_TIME_DELTA = 1000;
	public static final int DEFAULT_MINIMUM_FOR_BEEPING = 3000;
	private CountdownView m_countdownView;
	private TextView m_txtTimeLeft;
	private TextView m_txtRoundName;
	private TimerTask m_timer;
	private MediaPlayer m_ShortBeep;
	private MediaPlayer m_LongBeep;
	private boolean m_bOnlyInitialBeep;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		initBeepPlayer(getApplicationContext());
		setContentView(R.layout.activity_countdown);
		int roundsAmount = getIntent().getIntExtra(SportTimerActivity.ROUNDS_AMOUNT, 0);
		int restDuration = getIntent().getIntExtra(SportTimerActivity.REST_DURATION, 0);
		int roundDuration = getIntent().getIntExtra(SportTimerActivity.ROUND_DURATION, 0);
		if (restDuration < DEFAULT_MINIMUM_FOR_BEEPING || roundDuration < DEFAULT_MINIMUM_FOR_BEEPING) {
			m_bOnlyInitialBeep = true;
		}
		m_countdownView = (CountdownView) findViewById(R.id.timerView);
		m_txtTimeLeft = (TextView) findViewById(R.id.txt_digits);
		m_txtTimeLeft.setText(String.valueOf(roundDuration/MILLISEC_IN_SEC));
		m_txtRoundName = (TextView) findViewById(R.id.txt_name);
		m_countdownView.setRepeatsAmount(roundsAmount);
		m_timer  = new TimerTask(roundsAmount, roundDuration, restDuration);
		
		CountDownTimer delayBeforeFirstStart = new CountDownTimer(DEFAULT_MINIMUM_FOR_BEEPING, 100) {
			int nBeepTimeMillisec = DEFAULT_BEEP_TIME_BEFORE_END;
			
			@Override
			public void onTick(long millisUntilFinished) {
				if (millisUntilFinished < nBeepTimeMillisec) {
					if (m_ShortBeep != null) {
						m_ShortBeep.start();
						nBeepTimeMillisec -= DEFAULT_BEEP_TIME_DELTA;
					}
				}
			}
			
			@Override
			public void onFinish() {
				if (m_LongBeep != null) {
					m_LongBeep.start();
					m_timer.startTimer();
				}
			}
		};
		delayBeforeFirstStart.start();	
	}
	
	private void initBeepPlayer(Context context) {
		m_ShortBeep = MediaPlayer.create(context,R.raw.beep_025sec);
		m_LongBeep = MediaPlayer.create(context,R.raw.beep_05sec);
	}
	
	@Override
	protected void onDestroy() {
		m_countdownView.stopTimer();
		m_timer.stopTimers();
		m_LongBeep.release();
		m_LongBeep = null;
		m_ShortBeep.release();
		m_ShortBeep = null;
		super.onDestroy();
	}
	
	private class TimerTask {
		private int m_nInternalBeepTimeMillisec;
		private int m_nRoundsAmount;
		private int m_nRoundDuration;
		private int m_nRestDuration;
		private boolean m_bStopTimer = false;;
		
		private RepeatCountdownTimer m_Timer;
		private CountDownTimer m_pauseTimer;
		
		public TimerTask(int roundsAmount, int roundDuration, int restDuration) {
			m_nRoundsAmount = roundsAmount;
			m_nRoundDuration = roundDuration;
			m_nRestDuration = restDuration;
			m_nInternalBeepTimeMillisec = DEFAULT_BEEP_TIME_BEFORE_END;
			m_Timer = new RepeatCountdownTimer(m_nRoundDuration, 100);
		}
		
		public void stopTimers() {
			m_bStopTimer  = true;
			m_Timer.cancelPauseTimer();
			m_Timer.cancel();
		}

		public void startTimer() {
			m_countdownView.setPauseState(false);
			m_countdownView.startAnimateTimerExercise(m_nRoundDuration);
			m_Timer.start();
		}
		
		private class RepeatCountdownTimer extends CountDownTimer {			
			
			public RepeatCountdownTimer(long millisInFuture, long countDownInterval) {
				super(millisInFuture, countDownInterval);
				//Here we're setting up pause count down. Ticks for active counting are below.
				m_pauseTimer = new CountDownTimer(m_nRestDuration, countDownInterval) {					
					@Override
					public void onTick(long millisUntilFinished) {
						m_txtTimeLeft.setText(String.valueOf((millisUntilFinished / MILLISEC_IN_SEC + 1)));
						tick(millisUntilFinished);
					}
					
					@Override
					public void onFinish() {
						if (m_bStopTimer)
							return;
						m_nInternalBeepTimeMillisec = DEFAULT_BEEP_TIME_BEFORE_END;
						m_LongBeep.start();
						m_txtRoundName.setText("Work now!");
						startTimer();
					}
				};
				//Until here
			}
			
			private void tick(long millisUntilFinished) {
				if (m_bStopTimer)
					return;
				if (millisUntilFinished < m_nInternalBeepTimeMillisec && !m_bOnlyInitialBeep) {
					m_ShortBeep.start();
					m_nInternalBeepTimeMillisec -= DEFAULT_BEEP_TIME_DELTA;
				}
			}

			public void cancelPauseTimer() {
				m_pauseTimer.cancel();
			}

			@Override
			public void onTick(long millisUntilFinished) {
				m_txtTimeLeft.setText(String.valueOf((millisUntilFinished / MILLISEC_IN_SEC + 1)));
				tick(millisUntilFinished);
			}
			
			@Override
			public void onFinish() {
				if (m_bStopTimer)
					return;
				m_LongBeep.start();
				m_nInternalBeepTimeMillisec = DEFAULT_BEEP_TIME_BEFORE_END;
				m_nRoundsAmount--;
				if (m_nRoundsAmount > 0) {
					m_countdownView.setPauseState(true);
					m_txtRoundName.setText("Relax now!");
					m_pauseTimer.start();
					m_countdownView.startAnimateTimerExercise(m_nRestDuration);
				} else {
					m_countdownView.stopTimer();
					m_countdownView.setPauseState(true);
					m_txtRoundName.setText("Done!");
					m_txtTimeLeft.setText("");
				}
			}
		}
	}
}