
#include <jni.h>
#include <android/native_window_jni.h>
#include <android/log.h>
#include <vulkan/vulkan.h>
#include <vulkan/vulkan_android.h>
#include <cassert>
#include <vector>
#include <algorithm>

#include "co_device.h"
#include "co_hardware_buffers.h"

int COMPOSITOR_SOCK_FD = Vulkan::Compositor::createSocket();

Vulkan::Compositor Vulkan::Compositor::compositor;

ExternalMemoryAndroid external_mem_android;


VkResult Vulkan::Compositor::vkCreateVulkanAndroidSurface(VkInstance pInstance, const VkAndroidSurfaceCreateInfoKHR pSurfaceCreateInfo) {
    if (vkCreateAndroidSurfaceKHR(_instance, &pSurfaceCreateInfo, nullptr, &_surface) != VK_SUCCESS) {
        __android_log_print(ANDROID_LOG_ERROR, "VulkanCompositor", "Failed to create android surface");
    } else {
        return VK_SUCCESS;
    }
}

VkResult Vulkan::Compositor::vkCreateInstance(const VkInstanceCreateInfo* pCreateInfo, const VkAllocationCallbacks* pAllocator, VkInstance* pInstance) {
    if (vkCreateInstance(pCreateInfo, nullptr, pInstance) != VK_SUCCESS) {
        __android_log_print(ANDROID_LOG_ERROR, "VulkanCompositor", "Failed to create vulkan instance");
    } else {
        return VK_SUCCESS;
    }
}

void Vulkan::Compositor::init_vulkan() {
    VkApplicationInfo appInfo = {
        .sType = VK_STRUCTURE_TYPE_APPLICATION_INFO,
        .pApplicationName = "Aurora Compositor Application",
        .applicationVersion = VK_MAKE_VERSION(1, 0, 0),
        .pEngineName = "No Engine",
        .engineVersion = VK_MAKE_VERSION(1, 0, 0),
        .apiVersion = VK_API_VERSION_1_0
    };
    
    VkInstanceCreateInfo createInfo = {
        .sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO,
        .pApplicationInfo = &appInfo,
        .ppEnabledExtensionNames = required_extensions.data(),
        .enabledExtensionCount = static_cast<uint32_t>(required_extensions.size())
   };
   
   vkCreateInstance(&createInfo, nullptr, &_instance);
   
   VkAndroidSurfaceCreateInfoKHR surfaceCreateInfo = {
       .sType = VK_STRUCTURE_TYPE_ANDROID_SURFACE_CREATE_INFO_KHR,
       .window = window
   };
   
   vkCreateVulkanAndroidSurface(_instance, surfaceCreateInfo);
   
}

void Vulkan::Compositor::draw() {
    // receive new images to draw 
    external_mem_android.recv_image_buffers(COMPOSITOR_SOCK_FD);
    
    
}

void Vulkan::Compositor::run() {
    while(!stop_rendering) {
        draw();
    }
}

void Vulkan::Compositor::init(JNIEnv *env, jobject surface) {
    
    // create ANativeWindow from SurfaceView
    window = ANativeWindow_fromSurface(env, surface);
    if (!window) {
        __android_log_print(ANDROID_LOG_ERROR, "VulkanCompositor", "Failed to create ANativeWindow");
        return;
    }
    
     __android_log_print(ANDROID_LOG_INFO, "VulkanCompositor", "ANativeWindow created successfully");
    
    //init_vulkan();
    
    // alloc buffers
    //allocate_buffers();
    
    // run compositor
    //run();
}

void Vulkan::Compositor::cleanup() {
    // close socket
    Vulkan::Compositor::closeSocket(COMPOSITOR_SOCK_FD);
    
    external_mem_android.release_buffers();
    
    // destroy VkInstance
    vkDestroyInstance(_instance, nullptr);
}


