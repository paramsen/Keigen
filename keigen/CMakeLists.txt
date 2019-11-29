cmake_minimum_required(VERSION 3.4.1)

if(CMAKE_BUILD_TYPE STREQUAL Debug)
    message("DEBUG")
    set(debug 1)
else()
    message("RELEASE")
    set(debug 0)
endif()

add_library(keigen
        SHARED
        src/main/native/FloatMatrix.cpp
        src/main/native/Shared.cpp
        build/generated/source/keigen/main/native/LongMatrix.cpp
        build/generated/source/keigen/main/native/IntMatrix.cpp
        build/generated/source/keigen/main/native/DoubleMatrix.cpp
        build/generated/source/keigen/main/native/ShortMatrix.cpp
        build/generated/source/keigen/main/native/ByteMatrix.cpp
        build/generated/source/keigen/main/native/CharMatrix.cpp
        )

target_include_directories(keigen
        PRIVATE
        src/main/native/
        src/main/native/Eigen/Eigen)

target_compile_definitions(keigen PRIVATE
        DEBUG=${debug}
        # Limit to C++11 support
        EIGEN_MAX_CPP_VER=11
        # only complie resources with permissive licensing
        EIGEN_MPL2_ONLY=1)

if(NOT debug)
    target_compile_definitions(keigen PRIVATE EIGEN_NO_DEBUG=1)
endif()

target_link_libraries(keigen
        log)