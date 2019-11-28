#include <jni.h>
#include "TemplateMatrix.h"

extern "C" {
JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_initializeFill(JNIEnv *env, jclass jThis, jint rows, jint cols, jfloat fill) {
    return reinterpret_cast<jlong>(Keigen::initializeFill<float>(rows, cols, fill));
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_initializeWithData(JNIEnv *env, jclass jThis, jint rows, jint cols, jfloatArray jData, jint outerStride, jint innerStride) {
    float *rawData = env->GetFloatArrayElements(jData, nullptr);
    float *data = new float[rows * cols];
    memcpy(data, rawData, sizeof(float) * rows * cols);
    env->ReleaseFloatArrayElements(jData, rawData, 0);

    return reinterpret_cast<jlong>(Keigen::initializeWithData<float>(env, rows, cols, data, outerStride, innerStride));
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixPlus(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    return reinterpret_cast<jlong>(Keigen::matrixPlus<float>(pointerA, pointerB));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixPlusAssign(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    Keigen::matrixPlusAssign<float>(pointerA, pointerB);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixMinus(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    return reinterpret_cast<jlong>(Keigen::matrixMinus<float>(pointerA, pointerB));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixMinusAssign(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    Keigen::matrixMinusAssign<float>(pointerA, pointerB);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixTimes(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    return reinterpret_cast<jlong>(Keigen::matrixTimes<float>(pointerA, pointerB));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixTimesAssignRequireSquare(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    Keigen::matrixTimesAssignRequireSquare<float>(pointerA, pointerB);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixTimesIntoDst(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB, jlong pointerC) {
    Keigen::matrixTimesIntoDst<float>(pointerA, pointerB, pointerC);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixTimesScalar(JNIEnv *env, jclass jThis, jlong pointerA, jfloat scalar) {
    return reinterpret_cast<jlong>(Keigen::matrixTimesScalar<float>(pointerA, scalar));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixTimesAssignScalar(JNIEnv *env, jclass jThis, jlong pointerA, jfloat scalar) {
    Keigen::matrixTimesAssignScalar<float>(pointerA, scalar);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixDivScalar(JNIEnv *env, jclass jThis, jlong pointerA, jfloat scalar) {
    return reinterpret_cast<jlong>(Keigen::matrixDivScalar<float>(pointerA, scalar));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixDivAssignScalar(JNIEnv *env, jclass jThis, jlong pointerA, jfloat scalar) {
    Keigen::matrixDivAssignScalar<float>(pointerA, scalar);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixTranspose(JNIEnv *env, jclass jThis, jlong pointer) {
    return reinterpret_cast<jlong>(Keigen::matrixTranspose<float>(pointer));
}

JNIEXPORT jfloat JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_get(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col) {
    return Keigen::get<float>(pointer, row, col);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_set(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col, jfloat value) {
    Keigen::set<float>(pointer, row, col, value);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_getArray(JNIEnv *env, jclass jThis, jlong pointer, jfloatArray jDst) {
    Keigen::getArray<float>(env, pointer, jDst);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_setArray(JNIEnv *env, jclass jThis, jlong pointer, jfloatArray jSrc) {
    Keigen::setArray<float>(env, pointer, jSrc);
}
}
