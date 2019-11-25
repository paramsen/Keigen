#include <jni.h>
#include "Eigen/Eigen/Dense"
#include <android/log.h>
#include <algorithm>

using Eigen::Dynamic;
using Eigen::Map;
using Eigen::Matrix;

typedef Map<Matrix<float, Dynamic, Dynamic>> FloatMatrix;

extern "C" {
JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_initialize(JNIEnv *env, jclass jThis, jint rows, jint cols, jfloat fill) {
    auto *data = new float[rows * cols];

    //TODO find put where std::fill reside and use it
    for (int i = 0; i < rows * cols; i++) {
        data[i] = fill;
    }

    return reinterpret_cast<jlong>(new FloatMatrix(data, rows, cols));
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixPlus(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    auto a = *((FloatMatrix *) pointerA);
    auto b = *((FloatMatrix *) pointerB);
    auto data = new float[(a.rows()) * (a.cols())]{0.0F};
    auto c = new FloatMatrix(data, a.rows(), a.cols());
    c->noalias() = a + b;

    return reinterpret_cast<jlong>(c);
}

JNIEXPORT float JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_get(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col) {
    return (*((FloatMatrix *) pointer))(row, col);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_dispose(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col) {
    auto m = ((FloatMatrix *) pointer);
    float *data = &(*m)(0);
    delete (m);
    delete(data);
}
}
