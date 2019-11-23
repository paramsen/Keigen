cmake_minimum_required(VERSION 3.4.1)

if(CMAKE_BUILD_TYPE STREQUAL Debug)
    message("DEBUG")
    set(debug 1)
else()
    message("RELEASE")
    set(debug 0)
endif()

add_library(eigen
        SHARED
        eigen.cpp)

target_include_directories(eigen
        PRIVATE
        Eigen/Eigen)

target_compile_definitions(eigen PRIVATE
        DEBUG=${debug}
        # Limit to C++11 support
        EIGEN_MAX_CPP_VER=11
        # only complie resources with permissive licensing
        EIGEN_MPL2_ONLY=1)

target_link_libraries(eigen
        log)