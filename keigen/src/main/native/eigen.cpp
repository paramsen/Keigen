#include <jni.h>
#include "Eigen/Eigen/Dense"
#include <android/log.h>
#include <algorithm>

using Eigen::Dynamic;
using Eigen::Map;
using Eigen::Matrix;

extern "C" {
JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_initialize(JNIEnv *env, jclass jThis, jint rows,
                                                       jint cols, jfloat fill) {
    float *b = new float[rows * cols];
    for (int i = 0; i < rows * cols; i++) {
        b[i] = fill;
    }
    Map<Matrix<float, Dynamic, Dynamic>> *a = new Map<Matrix<float, Dynamic, Dynamic>>(b, rows, cols);
    __android_log_print(ANDROID_LOG_DEBUG, "NATIVE", "%.2f", a->operator()(0, 0));
    return reinterpret_cast<jlong>(a);
}

JNIEXPORT float JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_get(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col) {
    return ((Map<Matrix<float, Dynamic, Dynamic>> *) pointer)->operator()(row, col);
}
}
