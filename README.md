# android-opencl-example

  在 https://github.com/WhiteIsClosing/Android-OpenCL-Sobel-Filter 基础上修改而来  

# 使用

1.编译动态链接库
 + a.修改Android.mk,将OPENCV_ANDROID_SDK 替换成自己的路径 
 + b.修改build.sh,将MY_NDK_PATH 替换成自己的路径 
 + c. ./build.sh 进行编译 
  
2.将ImageSobelFilter1 导入AndroidStudio,即可. 

# 界面如图:

![example](https://github.com/printlner/android-opencl-example/blob/master/example.png)
