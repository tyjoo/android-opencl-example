package com.white.imagesobelfilter;
//导入opencv相关的包

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ImageSobelFilter extends Activity implements View.OnClickListener {
    private Button chooseButton, sobelGPU_CPU;
    private ImageView primaryImageView;
    private TextView tv_result,tvGpuInfo;
    private ListView image_list;
    private int RESULT_LOAD_IMAGE = 3;
    private nativeSobelFilter t;
    private String imagePathString;
    private Handler handler;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_sobel_filter);
        chooseButton = (Button) findViewById(R.id.choose_button);
        sobelGPU_CPU = (Button) findViewById(R.id.sobel_gpu_cpu);
        primaryImageView = (ImageView) findViewById(R.id.primary_image);

        tv_result = (TextView) findViewById(R.id.tv_result);
        tvGpuInfo= (TextView) findViewById(R.id.gpu_info);
        image_list = (ListView) findViewById(R.id.list);
        sobelGPU_CPU.setEnabled(false);
        chooseButton.setOnClickListener(this);
        sobelGPU_CPU.setOnClickListener(this);
        this.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                showResult((String) msg.obj);
            }
        };
        myAdapter = new MyAdapter(this);
        this.image_list.setAdapter(this.myAdapter);
    }

    private void showResult(String rs) {
        this.myAdapter.clear();
        this.tv_result.setText("result:\n" + rs);
        String prefix = FileUtil.getMyRoot();
        this.myAdapter.addData(new ListItem(prefix + "/Sobel_input_gray.jpg", "sobel_input_image"));
        this.myAdapter.addData(new ListItem(prefix + "/gpu_sobel.jpg", "sobel_gpu_image"));
        this.myAdapter.addData(new ListItem(prefix + "/cpu_sobel.jpg", "sobel_cpu_image"));
        this.myAdapter.addData(new ListItem(prefix + "/differenceImg.jpg", "sobel_differ_image"));
        this.myAdapter.notifyDataSetChanged();
        LoadDialog.dismiss(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_button:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            case R.id.sobel_gpu_cpu:
                LoadDialog.show(this);
                t = new nativeSobelFilter(imagePathString,handler);
                this.tvGpuInfo.setText("GPU:"+t.getPlatformName()+","+t.getDeviceName());
                t.start();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_sobel_filter, menu);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePathString = cursor.getString(columnIndex);
            cursor.close();
            Log.i("taoyj","--------> selected image:"+imagePathString);
            sobelGPU_CPU.setEnabled(true);
            primaryImageView.setImageBitmap(handleOOM(imagePathString,
                    primaryImageView.getWidth(),primaryImageView.getHeight()));
        }
    }

    //解决bitmap溢出的方法
    private Bitmap handleOOM(String absolutePath, int width,int height) {
        Bitmap bm;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        bm = BitmapFactory.decodeFile(absolutePath, opt);

        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1;

        // 根据imageview的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picWidth > width)
                opt.inSampleSize = picWidth / width;
        } else {
            if (picHeight > height)
                opt.inSampleSize = picHeight / height;
        }
        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(absolutePath, opt);
        return bm;
    }


    class MyAdapter extends BaseAdapter {
        private Context context;
        private List<ListItem> data;

        public MyAdapter(Context context) {
            this.context = context;
            this.data = new ArrayList<ListItem>();
        }
        public void clear(){
            this.data.clear();
        }

        public void addData(ListItem item) {
            this.data.add(item);
        }

        @Override
        public int getCount() {
            return this.data.size();
        }

        @Override
        public ListItem getItem(int position) {
            return this.data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHelper vh = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.context).inflate(R.layout.list_item, null);
                vh = new ViewHelper();
                vh.img = (ImageView) convertView.findViewById(R.id.image);
                vh.txt = (TextView) convertView.findViewById(R.id.image_text);
                convertView.setTag(vh);
            } else {
                vh = (ViewHelper) convertView.getTag();
            }
            ListItem item = getItem(position);
            vh.txt.setText(item.imgTxt);
            Log.i("taoyj",vh.img.getWidth()+","+vh.img
            .getHeight());
            vh.img.setImageBitmap(handleOOM(item.imgPath, 200,200));
            return convertView;
        }

        private class ViewHelper {
            ImageView img;
            TextView txt;
        }
    }

    class ListItem {
        String imgPath;
        String imgTxt;

        public ListItem(String imgPath, String imgTxt) {
            this.imgPath = imgPath;
            this.imgTxt = imgTxt;
        }
    }
}

