cmake_minimum_required(VERSION 3.10.2)

project("android_cascade_ndk")

set(opencvsourcedir C:\\opencv) #<-- You need to replace your libraries path here for make application working. C:\opencv is just an example and not works for you.

include_directories(${opencvsourcedir}/jni/include)
add_library(lib_opencv SHARED IMPORTED)
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${opencvsourcedir}/libs/${ANDROID_ABI}/libopencv_java4.so)


add_library(
        app
        SHARED
        app.cpp)



find_library(log-lib

        log )

target_link_libraries(
        app
        lib_opencv
        ${log-lib} )