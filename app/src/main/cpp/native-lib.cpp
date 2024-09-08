#include <jni.h>

#include "compositor/vulkan/co_device.h"
#include "android/log.h"

extern "C" {
    JNIEXPORT void JNICALL
    Java_com_project_1aurora_emu_NativeCode_surfaceChanged(JNIEnv *env, jobject obj, jobject surface, jint width, jint height) {
        if (&Vulkan::Compositor::Get() != nullptr) {
            Vulkan::Compositor::Get().init(env, surface);
            __android_log_print(ANDROID_LOG_INFO, "VulkanCompositor", "co device is created!!");
        } else {
            __android_log_print(ANDROID_LOG_INFO, "VulkanCompositor", "Instance is nullptr!!");
        }
        
    }
    
    JNIEXPORT void JNICALL
    Java_com_project_1aurora_emu_NativeCode_surfaceDestroyed(JNIEnv *env, jobject obj) {
        if (&Vulkan::Compositor::Get() != nullptr) {
            Vulkan::Compositor::Get().cleanup();
            __android_log_print(ANDROID_LOG_INFO, "VulkanCompositor", "co device is destroyed!!");
        } else {
            __android_log_print(ANDROID_LOG_INFO, "VulkanCompositor", "Instance is nullptr!!");
        }
        
    }
}