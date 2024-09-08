
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
#include <iostream>

#include "co_hardware_buffers.h"

void ExternalMemoryAndroid::allocate_buffers() {
    std::for_each(image_buffers.begin(), image_buffers.end(), [](ExternalMemoryAndroid::image_ahb_t image) {
        image.allocateBuffer();
    });
}

void ExternalMemoryAndroid::recv_image_buffers(int sock_fd) {
    ExternalMemoryAndroid::image_ahb_t new_img = {
        .sock_fd = sock_fd,
        .description = {
            // null for receive data ex: width, height, layer, usage...
        }
    };
        
    ExternalMemoryAndroid::receive_buffer(&new_img.description, &new_img.ahb, sock_fd);
    
    new_img.allocateBuffer();
    //add to image buffers 
    image_buffers.emplace_back(new_img);
}

void ExternalMemoryAndroid::release_buffers() {
    std::for_each(image_buffers.begin(), image_buffers.end(), [](ExternalMemoryAndroid::image_ahb_t image) {
        image.releaseBuffer();
    });
}