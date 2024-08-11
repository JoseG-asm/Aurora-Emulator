cmake_minimum_required(VERSION 3.10.2)

project(WSI LANGUAGES CXX)


add_subdirectory(deps/vulkan-headers EXCLUDE_ALL)
set(VULKAN_CXX_INCLUDE "deps/vulkan-headers/include")
include_directories(${VULKAN_CXX_INCLUDE})

set(X11_HEAD "deps/x11-headers")
include_directories(${X11_HEAD})

add_library(wsi_wrapper SHARED
            native-lib.cpp
            )

target_link_libraries(wsi_wrapper
    PRIVATE
    log
    android
    vulkan
)

target_include_directories(wsi_wrapper PRIVATE ${VULKAN_CXX_INCLUDE})
          
