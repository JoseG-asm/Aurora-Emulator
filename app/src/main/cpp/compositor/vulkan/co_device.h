#pragma once

#include <jni.h>
#include <android/native_window_jni.h>
#include <vulkan/vulkan.h>
#include <vulkan/vulkan_android.h>
#include <android/hardware_buffer.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <cstring>
#include <dlfcn.h>
#include <iostream>

#include "co_hardware_buffers.h"


/** 
 * Get funcs from system driver 
 */ 
namespace VulkanSystemDriver {
    typedef struct driver_module_t {
        public:
        void *libvulkan;
        
        template <typename T>
        T get_instance_proc_addr(const char *name) {
            T func = reinterpret_cast<T>(dlsym( libvulkan, name));
            if (func == nullptr) {
                // not found handle some error
            }
            
            return func;
        }
    } driver_module_t;
}    

namespace Vulkan {
    
class Compositor {
public:
    typedef struct vk_image_t {
        struct ExternalMemoryAndroid::image_ahb_t ahb_image = {};
        // others members
    } vk_image_t;

    VkInstance _instance;
	VkDevice _device;
    VkSurfaceKHR _surface;
    
	bool _isInitialized{ false };
	int _frameNumber {0};
	bool stop_rendering{ false };
    
	struct ANativeWindow *window;
    
    static Compositor compositor;
    
	static Compositor& Get() {
        return compositor;
    }
    
    void init(JNIEnv *env, jobject surface);

    void cleanup();

	void draw();

	void run();
    
    void stopRendering() {
        stop_rendering = true;
    }
    
    static int createSocket(std::string sock_path = "/tmp/socket") {
        int sock_fd = socket(AF_UNIX, SOCK_STREAM, 0);
        if (sock_fd < 0) { return -1; }

        struct sockaddr_un addr;
        std::memset(&addr, 0, sizeof(addr));
        addr.sun_family = AF_UNIX;
        std::strncpy(addr.sun_path, sock_path.c_str(), sizeof(addr.sun_path) - 1);

        unlink(sock_path.c_str());

        if (bind(sock_fd, (struct sockaddr*)&addr, sizeof(addr)) < 0) {
            close(sock_fd);
            return -1;
        }
        
        if (listen(sock_fd, 5) < 0) {
            close(sock_fd);
            return -1;
        }
        
        return sock_fd;
    }
    
    static void closeSocket(int sock_fd, std::string sock_path = "/tmp/socket") {
        if (sock_fd >= 0) {
            close(sock_fd);
            unlink(sock_path.c_str());  // Remove the socket file from the filesystem
        }
    }
    
    private:
    Compositor() = default;
    void init_vulkan();
    
    std::vector<const char*> required_extensions;
    
    std::vector<VkExtensionProperties> m_ext_props;
    
    VkResult vkCreateVulkanAndroidSurface(VkInstance pInstance, const VkAndroidSurfaceCreateInfoKHR pSurfaceCreateInfo);
    VkResult vkCreateInstance(const VkInstanceCreateInfo* pCreateInfo, const VkAllocationCallbacks* pAllocator, VkInstance* pInstance);
    VkResult vkCreateDevice(VkPhysicalDevice physicalDevice, const VkDeviceCreateInfo* pCreateInfo, const VkAllocationCallbacks* pAllocator, VkDevice* pDevice);
    
    };
}