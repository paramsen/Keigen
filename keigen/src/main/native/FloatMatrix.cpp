#include <jni.h>
#include "TemplateMatrix.h"

extern "C" {
JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_initializeFill(JNIEnv *env, jclass jThis, jint rows, jint cols, jfloat fill) {
    return reinterpret_cast<jlong>(Keigen::initializeFill<float>(rows, cols, fill));
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_initializeWithData(JNIEnv *env, jclass jThis, jint rows, jint cols, jfloatArray jData, jint outerStride, jint innerStride) {
    return reinterpret_cast<jlong>(Keigen::initializeWithData<float>(env, rows, cols, jData, outerStride, innerStride));
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixPlus(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    return reinterpret_cast<jlong>(Keigen::matrixPlus<float>(pointerA, pointerB));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixPlusAssign(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    Keigen::matrixPlusAssign<float>(pointerA, pointerB);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixMinus(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    return reinterpret_cast<jlong>(Keigen::matrixMinus<float>(pointerA, pointerB));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixMinusAssign(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    Keigen::matrixMinusAssign<float>(pointerA, pointerB);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixTimes(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    return reinterpret_cast<jlong>(Keigen::matrixTimes<float>(pointerA, pointerB));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixTimesAssignRequireSquare(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    Keigen::matrixTimesAssignRequireSquare<float>(pointerA, pointerB);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixTimesIntoDst(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB, jlong pointerC) {
    Keigen::matrixTimesIntoDst<float>(pointerA, pointerB, pointerC);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixTimesScalar(JNIEnv *env, jclass jThis, jlong pointerA, jfloat scalar) {
    return reinterpret_cast<jlong>(Keigen::matrixTimesScalar<float>(pointerA, scalar));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixTimesAssignScalar(JNIEnv *env, jclass jThis, jlong pointerA, jfloat scalar) {
    Keigen::matrixTimesAssignScalar<float>(pointerA, scalar);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixDivScalar(JNIEnv *env, jclass jThis, jlong pointerA, jfloat scalar) {
    return reinterpret_cast<jlong>(Keigen::matrixDivScalar<float>(pointerA, scalar));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixDivAssignScalar(JNIEnv *env, jclass jThis, jlong pointerA, jfloat scalar) {
    Keigen::matrixDivAssignScalar<float>(pointerA, scalar);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_matrixTranspose(JNIEnv *env, jclass jThis, jlong pointer) {
    return reinterpret_cast<jlong>(Keigen::matrixTranspose<float>(pointer));
}

JNIEXPORT float JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_get(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col) {
    return Keigen::get<float>(pointer, row, col);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_set(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col, jfloat value) {
    Keigen::set<float>(pointer, row, col, value);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_getArray(JNIEnv *env, jclass jThis, jlong pointer, jfloatArray jDst) {
    Keigen::getArray<float>(env, pointer, jDst);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_setArray(JNIEnv *env, jclass jThis, jlong pointer, jfloatArray jSrc) {
    Keigen::setArray<float>(env, pointer, jSrc);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridge_dispose(JNIEnv *env, jclass jThis, jlong pointer) {
    Keigen::dispose<float>(pointer);
}
}
