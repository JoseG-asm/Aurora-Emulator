
#include "vulkan.h"

#include <jni.h>
#include <android/native_window_jni.h>
#include <vulkan/vulkan_android.h>
#include <cassert>
#include <vector>
#include <algorithm> // Para std::for_each

typedef struct vk_image {
    struct ExternalMemoryAndroid::image_ahb_t ahb_image = {};
    // others members
} vk_image;

int COMPOSITOR_SOCK_FD = VulkanCompositor::createSocket("/tmp/socket");

VulkanCompositor* compositor;
VulkanCompositor& VulkanCompositor::Get() { return *compositor; }

std::vector<ExternalMemoryAndroid::image_ahb_t> image_buffers;

auto allocate_buffers = [] () -> void {
    //allocate buffers
    std::for_each(image_buffers.begin(), image_buffers.end(), [](ExternalMemoryAndroid::image_ahb_t image) {
        image.allocateBuffer();
    });
};

auto recv_image_buffers = [] () -> void {
    ExternalMemoryAndroid::image_ahb_t new_img = {
        .sock_fd = COMPOSITOR_SOCK_FD,
        .description = {
            // null for receive data ex: width, height, layer, usage...
        }
    };
        
    ExternalMemoryAndroid::receive_buffer(&new_img.description, &new_img.ahb, COMPOSITOR_SOCK_FD);
    
    new_img.allocateBuffer();
    //add to image buffers 
    image_buffers.emplace_back(new_img);
};

void VulkanCompositor::init(JNIEnv *env, jobject surface) {
    assert(compositor == nullptr);
    compositor = this;
    window = ANativeWindow_fromSurface(env, surface);
    allocate_buffers();
    
}

void VulkanCompositor::cleanup() {
    VulkanCompositor::closeSocket(COMPOSITOR_SOCK_FD);
    //release buffers 
    std::for_each(image_buffers.begin(), image_buffers.end(), [](ExternalMemoryAndroid::image_ahb_t image) {
        image.releaseBuffer();
    });
}

void VulkanCompositor::draw() {
    // receive new images to draw 
    recv_image_buffers();
    
    
}

void VulkanCompositor::run() {
    while(!stop_rendering) {
        draw();
    }
}

