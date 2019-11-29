#include <jni.h>
#include "TemplateMatrix.h"

extern "C" {

typedef jfloat JAVA_TYPE;
typedef jfloatArray JAVA_TYPE_ARRAY;
typedef float CPP_TYPE;

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_initializeFill(JNIEnv *env, jclass jThis, jint rows, jint cols, JAVA_TYPE fill) {
    return reinterpret_cast<jlong>(Keigen::initializeFill<CPP_TYPE>(rows, cols, fill));
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_initializeWithData(JNIEnv *env, jclass jThis, jint rows, jint cols, JAVA_TYPE_ARRAY jData, jint outerStride, jint innerStride) {
    CPP_TYPE *rawData = env->GetFloatArrayElements(jData, nullptr);
    CPP_TYPE *data = new CPP_TYPE[rows * cols];
    memcpy(data, rawData, sizeof(CPP_TYPE) * rows * cols);
    env->ReleaseFloatArrayElements(jData, rawData, 0);

    return reinterpret_cast<jlong>(Keigen::initializeWithData<CPP_TYPE>(env, rows, cols, data, outerStride, innerStride));
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixPlus(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    return reinterpret_cast<jlong>(Keigen::matrixPlus<CPP_TYPE>(pointerA, pointerB));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixPlusAssign(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    Keigen::matrixPlusAssign<CPP_TYPE>(pointerA, pointerB);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixMinus(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    return reinterpret_cast<jlong>(Keigen::matrixMinus<CPP_TYPE>(pointerA, pointerB));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixMinusAssign(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    Keigen::matrixMinusAssign<CPP_TYPE>(pointerA, pointerB);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixTimes(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    return reinterpret_cast<jlong>(Keigen::matrixTimes<CPP_TYPE>(pointerA, pointerB));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixTimesAssignRequireSquare(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB) {
    Keigen::matrixTimesAssignRequireSquare<CPP_TYPE>(pointerA, pointerB);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixTimesIntoDst(JNIEnv *env, jclass jThis, jlong pointerA, jlong pointerB, jlong pointerC) {
    Keigen::matrixTimesIntoDst<CPP_TYPE>(pointerA, pointerB, pointerC);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixTimesScalar(JNIEnv *env, jclass jThis, jlong pointerA, JAVA_TYPE scalar) {
    return reinterpret_cast<jlong>(Keigen::matrixTimesScalar<CPP_TYPE>(pointerA, scalar));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixTimesAssignScalar(JNIEnv *env, jclass jThis, jlong pointerA, JAVA_TYPE scalar) {
    Keigen::matrixTimesAssignScalar<CPP_TYPE>(pointerA, scalar);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixDivScalar(JNIEnv *env, jclass jThis, jlong pointerA, JAVA_TYPE scalar) {
    return reinterpret_cast<jlong>(Keigen::matrixDivScalar<CPP_TYPE>(pointerA, scalar));
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixDivAssignScalar(JNIEnv *env, jclass jThis, jlong pointerA, JAVA_TYPE scalar) {
    Keigen::matrixDivAssignScalar<CPP_TYPE>(pointerA, scalar);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_matrixTranspose(JNIEnv *env, jclass jThis, jlong pointer) {
    return reinterpret_cast<jlong>(Keigen::matrixTranspose<CPP_TYPE>(pointer));
}

JNIEXPORT JAVA_TYPE JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_get(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col) {
    return Keigen::get<CPP_TYPE>(pointer, row, col);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_set(JNIEnv *env, jclass jThis, jlong pointer, jint row, jint col, JAVA_TYPE value) {
    Keigen::set<CPP_TYPE>(pointer, row, col, value);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_getArray(JNIEnv *env, jclass jThis, jlong pointer, JAVA_TYPE_ARRAY jDst) {
    CPP_TYPE *dst = env->GetFloatArrayElements(jDst, nullptr);
    Keigen::getArray<CPP_TYPE>(pointer, dst);
    env->ReleaseFloatArrayElements(jDst, dst, 0);
}

JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeFloat_setArray(JNIEnv *env, jclass jThis, jlong pointer, JAVA_TYPE_ARRAY jSrc) {
    CPP_TYPE *src = env->GetFloatArrayElements(jSrc, nullptr);
    Keigen::setArray<CPP_TYPE>(pointer, src);
    env->ReleaseFloatArrayElements(jSrc, src, 0);
}
}
