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
    auto *b = new float[rows * cols];

    //TODO find put where std::fill reside and use it
    for (int i = 0; i < rows * cols; i++) {
        b[i] = fill;
    }

    return reinterpret_cast<jlong>(new Map<Matrix<float, Dynamic, Dynamic>>(b, rows, cols));
}

JNIEXPORT float JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_get(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col) {
    return ((Map<Matrix<float, Dynamic, Dynamic>> *) pointer)->operator()(row, col);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_dispose(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col) {
    auto m = ((Map<Matrix<float, Dynamic, Dynamic>> *) pointer);
    float *data = &(*m)(0);
    delete (m);
    delete(data);
}
}
