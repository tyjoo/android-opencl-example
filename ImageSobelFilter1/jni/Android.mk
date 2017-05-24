LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include $(CLEAR_VARS)
OPENCV_LIB_TYPE :=STATIC
OPENCV_ANDROID_SDK := /home/taoyj/git/github/opencv/opencv/platforms/build_android_arm/install/
ifdef OPENCV_ANDROID_SDK
  ifneq ("","$(wildcard $(OPENCV_ANDROID_SDK)/OpenCV.mk)")
    include ${OPENCV_ANDROID_SDK}/OpenCV.mk
  else
    include ${OPENCV_ANDROID_SDK}/sdk/native/jni/OpenCV.mk
  endif
else
  include ../../sdk/native/jni/OpenCV.mk
endif

LOCAL_MODULE    := SobelFilter
LOCAL_CPPFLAGS	+= -I$(LOCAL_PATH)
LOCAL_SRC_FILES := ImageSobelFilter.cpp topencl.c
LOCAL_LDLIBS += -llog -ldl

include $(BUILD_SHARED_LIBRARY) 
 
