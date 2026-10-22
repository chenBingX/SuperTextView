cmake_minimum_required(VERSION 3.10)
project(GifLib LANGUAGES C CXX)

if(NOT ANDROID)
    message(FATAL_ERROR "Build GifLib with the Android NDK toolchain")
endif()

add_library(GifLib SHARED
    common.cpp decoder.cpp drawer.cpp gifinfo.cpp jni.cpp
    dgif_lib.c egif_lib.c gif_hash.c gifalloc.c reallocarray.c)
target_include_directories(GifLib PRIVATE "${CMAKE_CURRENT_SOURCE_DIR}")
set_target_properties(GifLib PROPERTIES
    C_STANDARD 99 CXX_STANDARD 11 CXX_STANDARD_REQUIRED YES)
target_compile_options(GifLib PRIVATE -O2 -fvisibility=hidden
    -ffunction-sections -fdata-sections)
# Both sizes matter: max-page-size alone can leave RELRO sharing a 16 KB page
# with writable .data/.bss, making JNI_OnLoad fault after the linker protects it.
set_property(TARGET GifLib APPEND_STRING PROPERTY LINK_FLAGS
    " -Wl,--gc-sections,--exclude-libs,ALL,-z,relro,-z,now,-z,max-page-size=16384,-z,common-page-size=16384")
target_link_libraries(GifLib PRIVATE jnigraphics log)
