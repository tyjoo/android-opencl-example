package com.white.imagesobelfilter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	private static FileUtil fu = null;
	private BufferedWriter bw = null;

	public static FileUtil getInstance(Context context) {
		if (null == fu) {
			fu = new FileUtil(context);
		}
		return fu;
	}

	public static String getMyRoot() {
		String sdCardRoot = Environment.getExternalStorageDirectory().getPath();// 获取跟目录
		return sdCardRoot + "/hamster/";
	}

	public static String getMyTestPicPath() {
		String sdCardRoot = Environment.getExternalStorageDirectory().getPath();// 获取跟目录
		File dir = new File(sdCardRoot + "/hamster/replay");
		return dir.getAbsolutePath();
	}

	private FileUtil(Context context) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (!sdCardExist) {
			Log.e("taoyj", "没有sd卡");
			return;
		}
		String sdCardRoot = Environment.getExternalStorageDirectory().getPath();// 获取跟目录
		Log.i("taoyj", sdCardRoot);
		if (null != sdCardRoot) {
			File dir = new File(sdCardRoot + "/hamster/statistics/");
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy_MM_dd_HH_mm_ss");
				String fileName = "fps_" + sdf.format(new Date()) + ".txt";
				if (!dir.exists()) {
					dir.mkdirs();
					new File(dir, fileName).createNewFile();
				}
				this.bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(new File(dir, fileName)), "utf-8"));
				this.bw.write("file created \r\n");
				Log.i("taoyj", "统计结果文件已打开");
			} catch (UnsupportedEncodingException e) {
				Log.e("taoyj", "sd 卡创建文件失败");
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				Log.e("taoyj", "sd 创建文件失败");
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("taoyj", "sd 创建文件失败");
				e.printStackTrace();
			}
		} else {
			Log.e("taoyj", "sd 卡获取失败");
		}
	}

	public void writeLine(String msg) {
		if (this.bw != null) {
			Log.i("taoyj", msg);
			try {
				this.bw.write(msg + "\r\n");
				this.bw.flush();
			} catch (IOException e) {
				Log.e("taoyj", "sd 写入失败" + e.getMessage());
				e.printStackTrace();
			}
		} else {
			Log.e("taoyj", "sd 写入失败,bw==null");
		}
	}

	public static void writeByteImgFile(String name, byte[] data) {
		File dir=new File(getMyRoot() + "/yuvs/");
		if(!dir.exists()) dir.mkdir();
		String path = getMyRoot() + "/yuvs/" + name;
		try {
			FileOutputStream fos = new FileOutputStream(new File(path));
			fos.write(data);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		if (this.bw != null) {
			try {
				this.bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
