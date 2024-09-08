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
#include <iostream>

class ExternalMemoryAndroid {
    public:
    
    typedef struct image_ahb_t {
        int sock_fd = -1;
        AHardwareBuffer* ahb = nullptr;
        AHardwareBuffer_Desc description = {};
        
        bool allocateBuffer() {
            int retval = AHardwareBuffer_allocate(&description, &ahb);
            return retval != 0 ? false : true;
        }
        
        void releaseBuffer() {
            AHardwareBuffer_release(ahb);
        }
    } image_ahb_t;
    
    std::vector<ExternalMemoryAndroid::image_ahb_t> image_buffers;

    bool send_buffer(AHardwareBuffer* ahb, int sock_fd) {
        int retval = AHardwareBuffer_sendHandleToUnixSocket(ahb, sock_fd);
        return retval != 0 ? false : true;
    }
    
    static bool receive_buffer(AHardwareBuffer_Desc* out_description, AHardwareBuffer** out_ahb, int sock_fd) {
        AHardwareBuffer* recv_ahb = nullptr;
        AHardwareBuffer_Desc recv_desc = {};
        
        
        int retval = AHardwareBuffer_recvHandleFromUnixSocket(sock_fd, &recv_ahb);
        
        
        AHardwareBuffer_describe(recv_ahb, &recv_desc);
        
        if (retval != 0 || recv_ahb == nullptr) {
            throw std::runtime_error("Failed to recv ahb`s android.");
        }
        
        *out_ahb = recv_ahb;
        *out_description = recv_desc;
        
        return retval != 0 || recv_ahb == nullptr ? false : true;
    }
    
    /**
    * @brief allocate buffers
    */
    void allocate_buffers();
    /**
    * @brief recv buffers from fd
    */
    void recv_image_buffers(int sock_fd);
    /**
    * @brief release buffers
    */
    void release_buffers();
};
