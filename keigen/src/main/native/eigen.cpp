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
    auto *data = new float[rows * cols];

    //TODO find put where std::fill reside and use it
    for (int i = 0; i < rows * cols; i++) {
        data[i] = fill;
    }

    return reinterpret_cast<jlong>(new Map<Matrix<float, Dynamic, Dynamic>>(data, rows, cols));
}
JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixPlus(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    auto a = *((Map<Matrix<float, Dynamic, Dynamic>> *) pointerA);
    auto b = *((Map<Matrix<float, Dynamic, Dynamic>> *) pointerB);
    auto data = new float[((int) a.rows()) * ((int) a.cols())]{0.0F};
    auto c = new Map<Matrix<float, Dynamic, Dynamic>>(data, (int) a.rows(), (int) a.cols());
    c->noalias() = a + b;

    return reinterpret_cast<jlong>(c);
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
