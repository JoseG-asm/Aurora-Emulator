cmake_minimum_required(VERSION 3.10.2)

project(WSI LANGUAGES CXX)


add_subdirectory(deps/vulkan-headers EXCLUDE_ALL)

set(VULKAN_CXX_INCLUDE "deps/vulkan-headers/include")
include_directories(${VULKAN_CXX_INCLUDE})


add_library(compositor_wrapper SHARED
            compositor/vulkan/co_device.cpp
            compositor/vulkan/co_device.h
            compositor/vulkan/co_hardware_buffers.cpp
            compositor/vulkan/co_hardware_buffers.h
            native-lib.cpp
            )

target_link_libraries(compositor_wrapper
    PRIVATE
    log
    android
    vulkan
)

target_include_directories(compositor_wrapper PRIVATE ${VULKAN_CXX_INCLUDE})
          
