
#include "vulkan.h"

#include <jni.h>
#include <android/native_window_jni.h>
#include <vulkan/vulkan_android.h>


VulkanCompositor compositor;

VulkanCompositor& VulkanCompositor::Get() { return *compositor; }

VulkanCompositor::init(JNIEnv *env, jobject surface) {
    assert(compositor == nullptr);
    compositor = this;
    window = ANativeWindow_fromSurface(env, surface);
    
    AHardwareBuffer* ahb
    ExternalMemoryAndroid external_memory_android;
    ExternalMemoryAndroid::image_ahb_t img = {
        .sock_fd = VulkanCompositor::createSocket(),
        .ahb = *ahb
    };
    
}

VulkanCompositor::cleanup() {
    
}

VulkanCompositor::draw() {
    
}

VulkanCompositor::run() {
    
}

