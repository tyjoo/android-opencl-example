package com.white.imagesobelfilter;

import android.os.Handler;
import android.os.Message;

public class nativeSobelFilter extends Thread{
	String imagePath;
	String outPath;
	Handler handler;
	public nativeSobelFilter(String imagePath, Handler handler) {
		this.imagePath = imagePath;
		this.outPath=FileUtil.getMyRoot();
		this.handler=handler;
		init();
	}

	static {
		System.loadLibrary("SobelFilter");
	}

	public void run() {
		String resultString=sobelFilter(imagePath,outPath);
		Message msg=Message.obtain();
		msg.obj=resultString;
		handler.sendMessage(msg);
	}

	public native String sobelFilter(String imagePath,String outPath);
	public native String getPlatformName();
	public native String getDeviceName();
	public native void init();
}
