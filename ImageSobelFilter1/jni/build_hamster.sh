MY_NDK_PATH=/home/taoyj/500g/tools/android-ndk-r12b
#if the directory name=jni
#$MY_NDK_PATH/ndk-build # NDK_DEBUG=1 
#else
$MY_NDK_PATH/ndk-build NDK_PROJECT_PATH=. NDK_APPLICATION_MK=Application.mk
#echo ">>copy to demo libs"
#cp libs/armeabi-v7a/* ../MTK_Demo/libs/armeabi-v7a/
#echo ">>finished copy"
echo ">>Done!"
