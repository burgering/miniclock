package com.teamake.miniclock;



import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color; 
public class MainActivity extends Activity {
	
	public final int TIMER_MODE_INIT = 0;
	public final int TIMER_MODE_TIMER = 1;
	public final int TIMER_MODE_TIMER_STOP = 2;
	public final int TIMER_MODE_COUNTDOWN = 3;
	public final int TIMER_MODE_COUNTDOWNRUN = 4;
	public final int TIMER_MODE_ALARMING = 5;
	public int timer_mode;
	
	public final int TIMER_MAX_COUNTDOWN = 120*60;//最大定时时间
	public int time_target; //定时时间(秒)
    public int time_minutes;  //显示时间(分)
    public int time_seconds;  //显示时间(秒)
    public int time_tick;     //每秒加1
    public int time_alarm; //响铃持续时间
    public int hour,minute;
    public boolean time_show=false;
    private Thread timethread;
    //public Timer timer;
    public int brightnessValue = 0; 
    TextView tv_min,tv_sec,tv_info,tv_dot;
    ImageButton bt1m,bt2m,bt5m,bt10m,bt30m,btstart,btsub,btset,btdsp;
    private MediaPlayer mMediaPlayer;
    
    public void timer_init(){
    	timer_mode = TIMER_MODE_INIT;
    	time_target = 0;
        time_minutes=0;
        time_seconds=0;
        time_tick=0;
        time_alarm=0;
        //timer = new Timer();
        //btstart.setImageDrawable(getResources().getDrawable(R.drawable.starttimer));
    }
    
	/* 响铃 */
	public void alarm_on(){
		Log.i("提示：", "xxx ALARM...ON.");
		mMediaPlayer.seekTo(0);
		mMediaPlayer.start();
	}
	/* 停止 */
	public void alarm_off(){
		Log.i("提示：", "xxx ALARM...OFF.");
		mMediaPlayer.pause();
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        final WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        //固定亮度
		//Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		//lp.screenBrightness = (float) 0.5;  
        //getWindow().setAttributes(lp);
        final Calendar c = Calendar.getInstance();
        //--------------------
        bt1m = (ImageButton)findViewById(R.id.button1m);
        bt2m = (ImageButton)findViewById(R.id.button2m);
        bt5m = (ImageButton)findViewById(R.id.button5m);
        bt10m = (ImageButton)findViewById(R.id.button10m);
        bt30m = (ImageButton)findViewById(R.id.button30m);
        btstart = (ImageButton)findViewById(R.id.buttonstart);
        btsub = (ImageButton)findViewById(R.id.buttonsub);
        //btset = (ImageButton)findViewById(R.id.setup);
        btdsp = (ImageButton)findViewById(R.id.buttondisplay);
        
        tv_min = (TextView)findViewById(R.id.minutes);
        tv_sec = (TextView)findViewById(R.id.seconds);
        tv_dot = (TextView)findViewById(R.id.textView3);
        tv_info = (TextView)findViewById(R.id.info);
        Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/Helvetica Bold.ttf");
        tv_min.setTypeface(typeFace);
        tv_sec.setTypeface(typeFace);
        
        timer_init();
        mMediaPlayer = MediaPlayer.create(this, R.raw.ring);

    	
        /* 创建线程 */
    	timethread = new Thread(new Runnable(){
    		
            Handler handler = new Handler(){
            	@Override
            	public void handleMessage(Message msg){
            		if(0xff == msg.what) {
            			if(timer_mode == TIMER_MODE_TIMER){
            				time_tick++;
            			}
            			if((timer_mode == TIMER_MODE_COUNTDOWNRUN)&&(time_target>0)){
            				time_target --;
            			}
            			if((timer_mode == TIMER_MODE_ALARMING)&&(time_alarm>0)){
            				time_alarm --;
            			}
            			
            		}
            		if(timer_mode == TIMER_MODE_INIT){
            			time_tick=0;
            		}
            		if((timer_mode == TIMER_MODE_INIT)||(timer_mode == TIMER_MODE_TIMER)||(timer_mode == TIMER_MODE_TIMER_STOP)){
            			time_minutes = time_tick/60;
                		time_seconds = time_tick - time_minutes*60;
                		if(false == time_show){
                			if(time_minutes<10){
                    			tv_min.setText("0"+Integer.toString(time_minutes));
                    		}
                    		else{
                    			tv_min.setText(Integer.toString(time_minutes));
                    		}
                    		if(time_seconds<10){
                    			tv_sec.setText("0"+Integer.toString(time_seconds));
                    		}
                    		else{
                    			tv_sec.setText(Integer.toString(time_seconds));
                    		}
                		}
            		}
            		if((timer_mode == TIMER_MODE_COUNTDOWNRUN)||(timer_mode == TIMER_MODE_COUNTDOWN)){
            			if((timer_mode == TIMER_MODE_COUNTDOWNRUN)&&(time_target==0)){
            				//倒计时时间到
            				timer_init();
            				timer_mode = TIMER_MODE_ALARMING;
            				time_alarm = 5;
            				btstart.setImageDrawable(getResources().getDrawable(R.drawable.timeup));
            				alarm_on();
            			}
            			time_minutes = time_target/60;
            			time_seconds = time_target - time_minutes*60;
            			if(false == time_show){
            				if(time_minutes<10){
                    			tv_min.setText("0"+Integer.toString(time_minutes));
                    		}
                    		else{
                    			tv_min.setText(Integer.toString(time_minutes));
                    		}
                    		if(time_seconds<10){
                    			tv_sec.setText("0"+Integer.toString(time_seconds));
                    		}
                    		else{
                    			tv_sec.setText(Integer.toString(time_seconds));
                    		}
            			}
            		}
            		if((timer_mode == TIMER_MODE_ALARMING)&&(time_alarm==0)){
        				timer_init();
        				timer_mode = TIMER_MODE_INIT;
        				btstart.setImageDrawable(getResources().getDrawable(R.drawable.starttimer));
        				alarm_off();
        			}

            		super.handleMessage(msg);
            		}
            };

    		@Override
    		public void run(){
    			
    			while(!Thread.currentThread().isInterrupted()){
    				try{
    					//Log.i("提示：", "RUN...");
    					/* 每一秒向handler发一个messsage */
    					Thread.sleep(1000);  
    					Message m = handler.obtainMessage();
    					m.what = 0xff;
    					handler.sendMessage(m);
    				}catch(Exception e) {  
                        e.printStackTrace();  
                        System.out.println("thread error...");  
                    }  
    			}
    			//Log.i("提示：", "FINISH...");
    		}
    	});
    	timethread.start();	

    	
        /* 按键处理 */
    	bt1m.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if((timer_mode == TIMER_MODE_INIT)||(timer_mode == TIMER_MODE_COUNTDOWN)){
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m1_light));
					}
					else{
						((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m1));
					}
				}
				else{
					//timer_mode = TIMER_MODE_TIMER
					/* 在计时器状态下数字键不发光 */
					//((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m1_dim));
				}

				return false;
			}
		});
  	
        bt1m.setOnClickListener(new View.OnClickListener() {
			
        	
			@Override
			public void onClick(View v) {
				if(timer_mode == TIMER_MODE_INIT){
					timer_mode = TIMER_MODE_COUNTDOWN;
					btstart.setImageDrawable(getResources().getDrawable(R.drawable.startcountdown));
				}
					
				if(timer_mode == TIMER_MODE_COUNTDOWN){
					if(time_target<TIMER_MAX_COUNTDOWN){
						time_target += 60;
						time_minutes += 1;
						if(time_minutes<10){
                			tv_min.setText("0"+Integer.toString(time_minutes));
                		}
                		else{
                			tv_min.setText(Integer.toString(time_minutes));
                		}
						tv_info.setText("");
					}

				}
				else{
					return;
				}

				
			}
		});
 
    	bt2m.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if((timer_mode == TIMER_MODE_INIT)||(timer_mode == TIMER_MODE_COUNTDOWN)){
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m2_light));
					}
					else{
						((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m2));
					}
				}
				else{
					//timer_mode = TIMER_MODE_TIMER
					/* 在计时器状态下数字键不发光 */
					//((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m2_dim));
				}

				return false;
			}
		});
        bt2m.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(timer_mode == TIMER_MODE_INIT){
					timer_mode = TIMER_MODE_COUNTDOWN;
					btstart.setImageDrawable(getResources().getDrawable(R.drawable.startcountdown));
				}
				if(timer_mode == TIMER_MODE_COUNTDOWN){
					if(time_target<TIMER_MAX_COUNTDOWN){
						time_target += 120;
						time_minutes += 2;
						if(time_minutes<10){
                			tv_min.setText("0"+Integer.toString(time_minutes));
                		}
                		else{
                			tv_min.setText(Integer.toString(time_minutes));
                		}
						tv_info.setText("");
					}

				}
				else{
					return;
				}
				
			}
		});
    	bt5m.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if((timer_mode == TIMER_MODE_INIT)||(timer_mode == TIMER_MODE_COUNTDOWN)){
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m5_light));
					}
					else{
						((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m5));
					}
				}
				else{
					//timer_mode = TIMER_MODE_TIMER
					/* 在计时器状态下数字键不发光 */
					//((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m5_dim));
				}

				return false;
			}
		});
        bt5m.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(timer_mode == TIMER_MODE_INIT){
					timer_mode = TIMER_MODE_COUNTDOWN;
					btstart.setImageDrawable(getResources().getDrawable(R.drawable.startcountdown));
				}
				if(timer_mode == TIMER_MODE_COUNTDOWN){
					if(time_target<TIMER_MAX_COUNTDOWN){
						time_target += 300;
						time_minutes += 5;
						if(time_minutes<10){
                			tv_min.setText("0"+Integer.toString(time_minutes));
                		}
                		else{
                			tv_min.setText(Integer.toString(time_minutes));
                		}
						tv_info.setText("");
					}
				}
				else{
					return;
				}
				
			}
		});
    	bt10m.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if((timer_mode == TIMER_MODE_INIT)||(timer_mode == TIMER_MODE_COUNTDOWN)){
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m10_light));
					}
					else{
						((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m10));
					}
				}
				else{
					//timer_mode = TIMER_MODE_TIMER
					/* 在计时器状态下数字键不发光 */
					//((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m10_dim));
				}

				return false;
			}
		});
        bt10m.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(timer_mode == TIMER_MODE_INIT){
					timer_mode = TIMER_MODE_COUNTDOWN;
					btstart.setImageDrawable(getResources().getDrawable(R.drawable.startcountdown));
				}
				if(timer_mode == TIMER_MODE_COUNTDOWN){
					if(time_target<TIMER_MAX_COUNTDOWN){
						time_target += 600;
						time_minutes += 10;
						if(time_minutes<10){
                			tv_min.setText("0"+Integer.toString(time_minutes));
                		}
                		else{
                			tv_min.setText(Integer.toString(time_minutes));
                		}
						tv_info.setText("");
					}

				}
				else{
					return;
				}
				
			}
		});
    	bt30m.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if((timer_mode == TIMER_MODE_INIT)||(timer_mode == TIMER_MODE_COUNTDOWN)){
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m30_light));
					}
					else{
						((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m30));
					}
				}
				else{
					//timer_mode = TIMER_MODE_TIMER
					/* 在计时器状态下数字键不发光 */
					//((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.m30_dim));
				}

				return false;
			}
		});
        bt30m.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(timer_mode == TIMER_MODE_INIT){
					timer_mode = TIMER_MODE_COUNTDOWN;
					btstart.setImageDrawable(getResources().getDrawable(R.drawable.startcountdown));
				}
				if(timer_mode == TIMER_MODE_COUNTDOWN){
					if(time_target<TIMER_MAX_COUNTDOWN){
						time_target += 1800;
						time_minutes += 30;
						if(time_minutes<10){
                			tv_min.setText("0"+Integer.toString(time_minutes));
                		}
                		else{
                			tv_min.setText(Integer.toString(time_minutes));
                		}
						tv_info.setText("");
					}
				}
				else{
					return;
				}
				
			}
		});
    	btsub.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if((timer_mode == TIMER_MODE_INIT)||(timer_mode == TIMER_MODE_COUNTDOWN)){
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.cl_light));
					}
					else{
						((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.cl));
					}
				}
				else{
					//timer_mode = TIMER_MODE_TIMER
					/* 在计时器状态下数字键不发光 */
					//((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.cl_dim));
				}

				return false;
			}
		});
        btsub.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if((timer_mode == TIMER_MODE_COUNTDOWN)||(timer_mode == TIMER_MODE_COUNTDOWNRUN)){
					timer_init();
					btstart.setImageDrawable(getResources().getDrawable(R.drawable.starttimer));

				}
					return;
			}
		});
        btstart.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(timer_mode == TIMER_MODE_INIT){
					//初始化状态显示“开始计时”。
					//((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.starttimer));
				}
				if(timer_mode == TIMER_MODE_TIMER){
					//计时器状态显示“停止计时”。
					//((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.stoptimer));
				}
				if(timer_mode == TIMER_MODE_TIMER_STOP){
					//计时器状态显示“清零”。
					//((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.cleartimer));
				}
				if(timer_mode == TIMER_MODE_COUNTDOWN){
					//倒计时模式显示“开始倒数”
					//((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.startcountdown));
				}
				if(timer_mode == TIMER_MODE_COUNTDOWNRUN){
					//倒计时模式显示“停止倒数”
					//((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.stopcountdown));
				}
				
				return false;
			}
		});
        btstart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/* 计时器 */
				if(timer_mode == TIMER_MODE_INIT){
					//初始化状态按下启动键进入计时器状态
					timer_mode = TIMER_MODE_TIMER;
					((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.stoptimer));
					/* 数字键变暗 */
					bt1m.setImageDrawable(getResources().getDrawable(R.drawable.m1_dim));
					bt2m.setImageDrawable(getResources().getDrawable(R.drawable.m2_dim));
					bt5m.setImageDrawable(getResources().getDrawable(R.drawable.m5_dim));
					bt10m.setImageDrawable(getResources().getDrawable(R.drawable.m10_dim));
					bt30m.setImageDrawable(getResources().getDrawable(R.drawable.m30_dim));
					btsub.setImageDrawable(getResources().getDrawable(R.drawable.cl_dim));
					return;
				}
				if(timer_mode == TIMER_MODE_TIMER){
					//计时器状态按下启动键进入计时停止状态
					timer_mode = TIMER_MODE_TIMER_STOP;
					((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.cleartimer));
					return;
				}
				if(timer_mode == TIMER_MODE_TIMER_STOP){
					//计时器停止状态按下启动键，计数器清零，进入计时初始状态
					timer_mode = TIMER_MODE_INIT;
					timer_init();
					((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.starttimer));
					/* 数字键变正常 */
					bt1m.setImageDrawable(getResources().getDrawable(R.drawable.m1));
					bt2m.setImageDrawable(getResources().getDrawable(R.drawable.m2));
					bt5m.setImageDrawable(getResources().getDrawable(R.drawable.m5));
					bt10m.setImageDrawable(getResources().getDrawable(R.drawable.m10));
					bt30m.setImageDrawable(getResources().getDrawable(R.drawable.m30));
					btsub.setImageDrawable(getResources().getDrawable(R.drawable.cl));
					
            		tv_min.setText("00");
            		tv_sec.setText("00");
					return;
				}
				/* 倒计时器 */
				if(timer_mode == TIMER_MODE_COUNTDOWN){
					//倒计时器状态按下启动键进入倒计时运行状态
					timer_mode = TIMER_MODE_COUNTDOWNRUN;
					((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.stopcountdown));
					return;
				}
				if(timer_mode == TIMER_MODE_COUNTDOWNRUN){
					timer_mode = TIMER_MODE_INIT;
					timer_init();
					((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.starttimer));
					return;
				}
			}
		});
        btdsp.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						hour = c.get(Calendar.HOUR_OF_DAY);
		                minute = c.get(Calendar.MINUTE);
		                time_show = true;
		                //Log.i("提示：", "xxx Touch.."+hour+":"+minute);
		                tv_min.setTextColor(Color.parseColor("#e68233"));
		                tv_sec.setTextColor(Color.parseColor("#e68233"));
		                tv_dot.setTextColor(Color.parseColor("#e68233"));
		                
		                if(hour<10){
		        			tv_min.setText("0"+Integer.toString(hour));
		        		}
		        		else{
		        			tv_min.setText(Integer.toString(hour));
		        		}
		        		if(minute<10){
		        			tv_sec.setText("0"+Integer.toString(minute));
		        		}
		        		else{
		        			tv_sec.setText(Integer.toString(minute));
		        		}
					}
					else{
						time_show = false;
						tv_min.setTextColor(Color.parseColor("#64b844"));
		                tv_sec.setTextColor(Color.parseColor("#64b844"));
		                tv_dot.setTextColor(Color.parseColor("#64b844"));
		                
						if((timer_mode == TIMER_MODE_INIT)||(timer_mode == TIMER_MODE_TIMER)||(timer_mode == TIMER_MODE_TIMER_STOP)){
	            			time_minutes = time_tick/60;
	                		time_seconds = time_tick - time_minutes*60;
	                		if(false == time_show){
	                			if(time_minutes<10){
	                    			tv_min.setText("0"+Integer.toString(time_minutes));
	                    		}
	                    		else{
	                    			tv_min.setText(Integer.toString(time_minutes));
	                    		}
	                    		if(time_seconds<10){
	                    			tv_sec.setText("0"+Integer.toString(time_seconds));
	                    		}
	                    		else{
	                    			tv_sec.setText(Integer.toString(time_seconds));
	                    		}
	                		}
	            		}
	            		if((timer_mode == TIMER_MODE_COUNTDOWNRUN)||(timer_mode == TIMER_MODE_COUNTDOWN)){
	            			if((timer_mode == TIMER_MODE_COUNTDOWNRUN)&&(time_target==0)){
	            				//倒计时时间到
	            				timer_init();
	            				timer_mode = TIMER_MODE_ALARMING;
	            				time_alarm = 5;
	            				btstart.setImageDrawable(getResources().getDrawable(R.drawable.timeup));
	            				alarm_on();
	            			}
	            			time_minutes = time_target/60;
	            			time_seconds = time_target - time_minutes*60;
	            			if(false == time_show){
	            				if(time_minutes<10){
	                    			tv_min.setText("0"+Integer.toString(time_minutes));
	                    		}
	                    		else{
	                    			tv_min.setText(Integer.toString(time_minutes));
	                    		}
	                    		if(time_seconds<10){
	                    			tv_sec.setText("0"+Integer.toString(time_seconds));
	                    		}
	                    		else{
	                    			tv_sec.setText(Integer.toString(time_seconds));
	                    		}
	            			}
	            		}
						
					}

				return false;
			}
		});
        
if(false)
{   
        btset.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/* 启动另一个Activity */
				Intent intent =
	                    new Intent(MainActivity.this.getApplication(),
	                            Setup.class);
	            intent.putExtra(null,0 );
	            startActivity(intent);
				
			}
		});
        
    }
    }
}
