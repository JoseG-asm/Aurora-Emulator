#pragma once

#include <jni.h>
#include <android/native_window_jni.h>
#include <vulkan/vulkan_android.h>

class VulkanCompositor {
public:
    VkInstance _instance;
	VkDevice _device;
    VkSurfaceKHR _surface;
    
	bool _isInitialized{ false };
	int _frameNumber {0};
	bool stop_rendering{ false };
    
	struct ANativeWindow *window;
    
	static VulkanCompositor& Get();
    
	void init(JNIEnv *env, jobject surface);

	void cleanup();

	void draw();

	void run();
    
    void stopRendering() {
        stop_rendering = true;
    }
    
    VkSurfaceKHR createVulkanAndroidSurface(ANativeWindow* window) {
        
        VkAndroidSurfaceCreateInfoKHR surfaceCreateInfo = {};
        surfaceCreateInfo.sType = VK_STRUCTURE_TYPE_ANDROID_SURFACE_CREATE_INFO_KHR;
        surfaceCreateInfo.window = window;

        if (vkCreateAndroidSurfaceKHR(_instance, &surfaceCreateInfo, nullptr, &_surface) != VK_SUCCESS) {
            throw std::runtime_error("Failed to create Vulkan Android Surface.");
        }

        return _surface;
    }
    
private:
	
};