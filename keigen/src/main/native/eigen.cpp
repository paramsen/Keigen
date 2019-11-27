#include <jni.h>
#include "Eigen/Eigen/Dense"
#include <android/log.h>
#include <algorithm>

using Eigen::Dynamic;
using Eigen::Map;
using Eigen::Matrix;

#define debugLog(...) __android_log_print(ANDROID_LOG_DEBUG, "NATIVE", __VA_ARGS__);

typedef Eigen::Stride<Dynamic, Dynamic> Stride;
typedef Map<Matrix<float, Dynamic, Dynamic>, 0, Stride> FloatMatrix;

extern "C" {
JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_initializeFill(JNIEnv *env, jclass jThis, jint rows, jint cols, jfloat fill) {
    auto *data = new float[rows * cols];

    //TODO find put where std::fill reside and use it
    for (int i = 0; i < rows * cols; i++) {
        data[i] = fill;
    }

    return reinterpret_cast<jlong>(new FloatMatrix(data, rows, cols, Stride(1, cols)));
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_initializeWithData(JNIEnv *env, jclass jThis, jint rows, jint cols, jfloatArray jData, jint outerStride, jint innerStride) {
    float *rawData = env->GetFloatArrayElements(jData, nullptr);
    float *data = new float[rows * cols];
    memcpy(data, rawData, sizeof(float) * rows * cols);
    env->ReleaseFloatArrayElements(jData, rawData, 0);

    return reinterpret_cast<jlong>(new FloatMatrix(data, rows, cols, Stride(outerStride, innerStride)));
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixPlus(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    auto a = *((FloatMatrix *) pointerA);
    auto b = *((FloatMatrix *) pointerB);
    auto data = new float[(a.rows()) * (a.cols())]{0};
    auto c = new FloatMatrix(data, a.rows(), a.cols(), Stride(1, a.cols()));
    *c = a + b;

    return reinterpret_cast<jlong>(c);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixPlusAssign(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    auto a = (FloatMatrix *) pointerA;
    auto b = (FloatMatrix *) pointerB;
    *a = ((*a) + (*b)).eval();
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixTimes(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    auto a = *((FloatMatrix *) pointerA);
    auto b = *((FloatMatrix *) pointerB);
    auto data = new float[(a.rows()) * (b.cols())]{0};
    auto c = new FloatMatrix(data, a.rows(), b.cols(), Stride(1, b.cols()));
    *c = a * b; //a temp var might be introduced here (?) https://eigen.tuxfamily.org/dox/group__TopicAliasing.html
    return reinterpret_cast<jlong>(c);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixTimesAssignRequireSquare(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    auto a = (FloatMatrix *) pointerA;
    auto b = (FloatMatrix *) pointerB;
    *a = (*a) * (*b);
}

JNIEXPORT float JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_get(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col) {
    return (*((FloatMatrix *) pointer))(row, col);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_set(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col, jfloat value) {
    (*((FloatMatrix *) pointer))(row, col) = value;
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_getArray(JNIEnv *env, jclass jThis, jlong pointer, jfloatArray jDst) {
    auto m = *((FloatMatrix *) pointer);
    float *src = &(m)(0, 0);
    float *dst = env->GetFloatArrayElements(jDst, nullptr);
    int length = env->GetArrayLength(jDst);
    memcpy(dst, src, sizeof(float) * length);
    env->ReleaseFloatArrayElements(jDst, dst, 0);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_setArray(JNIEnv *env, jclass jThis, jlong pointer, jfloatArray jSrc) {
    auto m = *((FloatMatrix *) pointer);
    float *dst = &(m)(0, 0);
    float *src = env->GetFloatArrayElements(jSrc, nullptr);
    int length = env->GetArrayLength(jSrc);
    memcpy(dst, src, sizeof(float) * length);
    env->ReleaseFloatArrayElements(jSrc, src, 0);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_dispose(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col) {
    auto m = ((FloatMatrix *) pointer);
    float *data = &(*m)(0, 0);
    delete (m);
    delete (data);
}
}
