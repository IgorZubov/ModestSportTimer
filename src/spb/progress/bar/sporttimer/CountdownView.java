package spb.progress.bar.sporttimer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class CountdownView extends View {
	private RectF m_RoundsCircle;
	private RectF m_TimerCircle;
	
	private Paint m_activityPaint = new Paint();
	private Paint m_restPaint = new Paint();
	private Paint m_activeSectorPaint = new Paint();
	private Paint m_finishedSectorPaint = new Paint();
	private Paint m_linePaint = new Paint();
	private Paint m_pointerPaint = new Paint();
	
	private Handler m_animationHandler; 
	
	private boolean m_isPauseCounter = false;
	private int m_nRoundsCircleRadius;
	private int m_nRoundsAmount;
	private int m_nDeltaAngle;
	private int m_nRoundsRemainsCounter;
	private int m_nCircleCenterX;
	private int m_nCircleCenterY;
	private int m_nLineWidth = 2;
	private int m_nOuterWheelWidth = 10;
	private boolean m_bTimerStop = false;

	private float m_fAnimationStep = 0;
	private float m_fCurrentProgress = 0;
	private long m_lExpectedFinishTime;
	private long m_lLastMeasuredTime = 0;
	
	public CountdownView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.CountdownView));
		m_bTimerStop = false;
		initView();
		getTimerCircleSize(context);
	}

	public CountdownView(Context context, AttributeSet attrs) {
		super(context, attrs);
		parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.CountdownView));
		m_bTimerStop = false;
		initView();
		getTimerCircleSize(context);
	}

	public CountdownView(Context context) {
		super(context);
		m_bTimerStop = false;
		initView();
		getTimerCircleSize(context);
	}
	
	@SuppressLint("HandlerLeak")
	private void initView() {
		setWillNotDraw(false);
		try {
			Method method = this.getClass().getMethod("setLayerType", new Class[] {int.class, Paint.class});
	        method.invoke(this, new Object[] {0, null});
	    }  catch (NoSuchMethodException e) {
	    	   e.printStackTrace();   
	    } catch (IllegalArgumentException e) {
	        e.printStackTrace();
	    } catch (IllegalAccessException e) {
	        e.printStackTrace();
	    } catch (InvocationTargetException e) {
	        e.printStackTrace();
	    }
	
		m_animationHandler = new Handler() {

			public void handleMessage(Message msg) {
				invalidate();
				long currentTime = System.currentTimeMillis();
				if (m_lExpectedFinishTime >= currentTime && !m_bTimerStop) {
					m_fCurrentProgress = m_fCurrentProgress - (m_fAnimationStep*(currentTime - m_lLastMeasuredTime));
					m_lLastMeasuredTime = System.currentTimeMillis();
					Log.d("COUNT", "now in: " + m_lLastMeasuredTime);
					m_animationHandler.sendEmptyMessage(0);
				}
			};
		};
	}
	
	private void parseAttributes(TypedArray a) {
		m_nLineWidth = (int) a.getDimension(R.styleable.CountdownView_sectorLineWidth, m_nLineWidth);
		m_nOuterWheelWidth = (int) a.getDimension(R.styleable.CountdownView_outerWheelWidth, m_nOuterWheelWidth);
        a.recycle();
    }

	private void getTimerCircleSize(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(metrics);
	}

	private void initPaints(int width, int height) {
		m_nRoundsCircleRadius = width/2 - m_nOuterWheelWidth+2;
		m_nCircleCenterX = width/2;
		m_nCircleCenterY = height/2;
		m_RoundsCircle = new RectF(m_nCircleCenterX - m_nRoundsCircleRadius, m_nCircleCenterY - m_nRoundsCircleRadius, 
				m_nCircleCenterX + m_nRoundsCircleRadius, m_nCircleCenterY + m_nRoundsCircleRadius);
		m_TimerCircle = new RectF(m_nOuterWheelWidth/2, m_nCircleCenterY - m_nCircleCenterX + m_nOuterWheelWidth/2, 
				2*m_nCircleCenterX - m_nOuterWheelWidth/2, m_nCircleCenterY + m_nCircleCenterX - m_nOuterWheelWidth/2);
				
		m_activityPaint.setColor(getResources().getColor(R.color.active_state));
		m_activityPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_activityPaint.setStyle(Style.STROKE);
		m_activityPaint.setStrokeWidth(m_nOuterWheelWidth);
		
		m_restPaint.setColor(getResources().getColor(R.color.pause_state));
		m_restPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_restPaint.setStyle(Style.STROKE);
		m_restPaint.setStrokeWidth(m_nOuterWheelWidth);
		
		
		m_activeSectorPaint.setColor(getResources().getColor(R.color.sector_remaining));
		m_activeSectorPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_activeSectorPaint.setStyle(Style.FILL);
		
		m_finishedSectorPaint.setColor(getResources().getColor(R.color.sector_done));
		m_finishedSectorPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_finishedSectorPaint.setStyle(Style.FILL);
		
		m_linePaint.setColor(getResources().getColor(R.color.sector_divider));
		m_linePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_linePaint.setStyle(Style.STROKE);
		m_linePaint.setStrokeWidth(m_nLineWidth);
		
		m_pointerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_pointerPaint.setColor(getResources().getColor(R.color.pointer));
		m_pointerPaint.setStyle(Style.STROKE);
		m_pointerPaint.setStrokeWidth(m_nOuterWheelWidth);
	}
	
	
	public void startAnimateTimerExercise(long milliSeconds) {
		m_fAnimationStep = 360f / milliSeconds;
		m_lLastMeasuredTime = System.currentTimeMillis();
		m_lExpectedFinishTime = m_lLastMeasuredTime + milliSeconds;
		m_fCurrentProgress = 0;
		m_animationHandler.sendEmptyMessage(0);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
	    int height = MeasureSpec.getSize(heightMeasureSpec);
		initPaints(width, height);
	    setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawArc(m_RoundsCircle, 0, 360, true, m_finishedSectorPaint);
		canvas.drawArc(m_RoundsCircle, m_nDeltaAngle * m_nRoundsRemainsCounter - 90, 360 - m_nDeltaAngle * m_nRoundsRemainsCounter,
				true, m_activeSectorPaint);
		drawSectorLines(canvas);
		if (m_isPauseCounter) {
			drawPauseState(canvas);
		} else {
			drawActiveState(canvas);
		}
		if (m_nRoundsRemainsCounter == 0) {
			m_fCurrentProgress = 360;
			drawActiveState(canvas);
		}
	}
	
	private void drawActiveState (Canvas canvas) {
		canvas.drawArc(m_TimerCircle, 0, 360, false, m_restPaint);
		canvas.drawArc(m_TimerCircle, -90, m_fCurrentProgress, false, m_activityPaint);
		canvas.drawArc(m_TimerCircle, m_fCurrentProgress-90, m_nLineWidth, false, m_pointerPaint);
	}
	
	private void drawPauseState (Canvas canvas) {
		canvas.drawArc(m_TimerCircle, 0, 360, false, m_activityPaint);
		canvas.drawArc(m_TimerCircle, -90, m_fCurrentProgress, false, m_restPaint);
		canvas.drawArc(m_TimerCircle, m_fCurrentProgress-90, m_nLineWidth, false, m_pointerPaint);
	}
	
	private void drawSectorLines (Canvas canvas) {
		for (int i = 0; i < m_nRoundsAmount; i++) {
			canvas.save();
			canvas.rotate(m_nDeltaAngle * i, m_nCircleCenterX, m_nCircleCenterY);
			canvas.drawLine(m_nCircleCenterX, m_nCircleCenterY, m_nCircleCenterX, m_nCircleCenterY - m_nRoundsCircleRadius,
					m_linePaint);
			canvas.restore();
		}
	}

	public void setPauseState(boolean isPauseCounter) {
		if (isPauseCounter) {
			m_nRoundsRemainsCounter--;
		}
		m_isPauseCounter = isPauseCounter;
	}
	
	public void stopTimer() {
//		m_animationHandler.disable();
	}

	public void setRepeatsAmount(int repeatsAmout) {
		m_nRoundsAmount = repeatsAmout;
		m_nDeltaAngle = 360/m_nRoundsAmount;
		m_nRoundsRemainsCounter = m_nRoundsAmount;
	}
}