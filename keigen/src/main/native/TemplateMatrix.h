//
// Created by Pär Amsen on 2019-11-27.
//

#ifndef KEIGEN_TEMPLATEMATRIX_H
#define KEIGEN_TEMPLATEMATRIX_H

#include <jni.h>
#include "Eigen/Eigen/Dense"
#include <android/log.h>

#define debugLog(...) __android_log_print(ANDROID_LOG_DEBUG, "NATIVE", __VA_ARGS__);

namespace Keigen {
    using Eigen::Dynamic;
    using Eigen::Map;

    typedef Eigen::Stride<Dynamic, Dynamic> Stride;
    template<typename Scalar>
    using Matrix = Map<Eigen::Matrix<Scalar, Dynamic, Dynamic>, 0, Stride>;

    template<typename Scalar>
    Matrix<Scalar> *initializeFill(jint rows, jint cols, jfloat fill) {
        auto *data = new Scalar[rows * cols];

        //TODO find put where std::fill reside and use it
        for (int i = 0; i < rows * cols; i++) {
            data[i] = fill;
        }

        return new Matrix<Scalar>(data, rows, cols, Stride(1, cols));
    }

    template<typename Scalar>
    Matrix<Scalar> *
    initializeWithData(JNIEnv *env, jint rows, jint cols, jfloatArray jData, jint outerStride,
                       jint innerStride) {
        float *rawData = env->GetFloatArrayElements(jData, nullptr);
        float *data = new float[rows * cols];
        memcpy(data, rawData, sizeof(float) * rows * cols);
        env->ReleaseFloatArrayElements(jData, rawData, 0);

        return new Matrix<Scalar>(data, rows, cols, Stride(outerStride, innerStride));
    }

    template<typename Scalar>
    Matrix<Scalar> *matrixPlus(jlong pointerA, jlong pointerB) {
        auto a = *((Matrix<Scalar> *) pointerA);
        auto b = *((Matrix<Scalar> *) pointerB);
        auto data = new float[(a.rows()) * (a.cols())]{0};
        auto c = new Matrix<Scalar>(data, a.rows(), a.cols(), Stride(1, a.cols()));
        *c = a + b;

        return c;
    }

    template<typename Scalar>
    void matrixPlusAssign(jlong pointerA, jlong pointerB) {
        auto a = (Matrix<Scalar> *) pointerA;
        auto b = (Matrix<Scalar> *) pointerB;
        *a = ((*a) + (*b)).eval();
    }

    template<typename Scalar>
    Matrix<Scalar> *matrixMinus(jlong pointerA, jlong pointerB) {
        auto a = *((Matrix<Scalar> *) pointerA);
        auto b = *((Matrix<Scalar> *) pointerB);
        auto data = new float[(a.rows()) * (a.cols())]{0};
        auto c = new Matrix<Scalar>(data, a.rows(), a.cols(), Stride(1, a.cols()));
        *c = a - b;

        return c;
    }

    template<typename Scalar>
    void matrixMinusAssign(jlong pointerA, jlong pointerB) {
        auto a = (Matrix<Scalar> *) pointerA;
        auto b = (Matrix<Scalar> *) pointerB;
        *a = ((*a) - (*b)).eval();
    }

    template<typename Scalar>
    Matrix<Scalar> *matrixTimes(jlong pointerA, jlong pointerB) {
        auto a = *((Matrix<Scalar> *) pointerA);
        auto b = *((Matrix<Scalar> *) pointerB);
        auto data = new float[(a.rows()) * (b.cols())]{0};
        auto c = new Matrix<Scalar>(data, a.rows(), b.cols(), Stride(1, b.cols()));
        *c = a * b; //a temp var might be introduced here (?) https://eigen.tuxfamily.org/dox/group__TopicAliasing.html
        return c;
    }

    template<typename Scalar>
    void matrixTimesAssignRequireSquare(jlong pointerA, jlong pointerB) {
        auto a = (Matrix<Scalar> *) pointerA;
        auto b = (Matrix<Scalar> *) pointerB;
        *a = (*a) * (*b);
    }

    template<typename Scalar>
    void matrixTimesIntoDst(jlong pointerA, jlong pointerB, jlong pointerC) {
        auto a = *((Matrix<Scalar> *) pointerA);
        auto b = *((Matrix<Scalar> *) pointerB);
        auto c = *((Matrix<Scalar> *) pointerC);
        c.noalias() = a * b; //disable aliasing
    }

    template<typename Scalar>
    Matrix<Scalar> *matrixTimesScalar(jlong pointerA, Scalar scalar) {
        auto a = *((Matrix<Scalar> *) pointerA);
        auto data = new float[(a.rows()) * (a.cols())]{0};
        auto c = new Matrix<Scalar>(data, a.rows(), a.cols(), Stride(1, a.cols()));
        *c = a * scalar;

        return c;
    }

    template<typename Scalar>
    void matrixTimesAssignScalar(jlong pointerA, Scalar scalar) {
        auto a = *((Matrix<Scalar> *) pointerA);
        a = a * scalar;
    }

    template<typename Scalar>
    Matrix<Scalar> *matrixDivScalar(jlong pointerA, Scalar scalar) {
        auto a = *((Matrix<Scalar> *) pointerA);
        auto data = new float[(a.rows()) * (a.cols())]{0};
        auto c = new Matrix<Scalar>(data, a.rows(), a.cols(), Stride(1, a.cols()));
        *c = a / scalar;

        return c;
    }

    template<typename Scalar>
    void matrixDivAssignScalar(jlong pointerA, Scalar scalar) {
        auto a = *((Matrix<Scalar> *) pointerA);
        a = a / scalar;
    }

    template<typename Scalar>
    Matrix<Scalar> *matrixTranspose(jlong pointer) {
        auto src = *((Matrix<Scalar> *) pointer);
        auto data = new float[(src.rows()) * (src.cols())]{0};
        auto dst = new Matrix<Scalar>(data, src.cols(), src.rows(), Stride(1, src.rows()));
        *dst = src.transpose();

        return dst;
    }

    template<typename Scalar>
    Scalar get(jlong pointer, jint row, jint col) {
        return (*((Matrix<Scalar> *) pointer))(row, col);
    }

    template<typename Scalar>
    void set(jlong pointer, jint row, jint col, jfloat value) {
        (*((Matrix<Scalar> *) pointer))(row, col) = value;
    }

    template<typename Scalar>
    void getArray(JNIEnv *env, jlong pointer, jfloatArray jDst) {
        auto m = *((Matrix<Scalar> *) pointer);
        float *src = &(m)(0, 0);
        float *dst = env->GetFloatArrayElements(jDst, nullptr);
        int length = env->GetArrayLength(jDst);
        memcpy(dst, src, sizeof(float) * length);
        env->ReleaseFloatArrayElements(jDst, dst, 0);
    }

    template<typename Scalar>
    void setArray(JNIEnv *env, jlong pointer, jfloatArray jSrc) {
        auto m = *((Matrix<Scalar> *) pointer);
        float *dst = &(m)(0, 0);
        float *src = env->GetFloatArrayElements(jSrc, nullptr);
        int length = env->GetArrayLength(jSrc);
        memcpy(dst, src, sizeof(float) * length);
        env->ReleaseFloatArrayElements(jSrc, src, 0);
    }

    template<typename Scalar>
    void dispose(jlong pointer) {
        auto m = ((Matrix<Scalar> *) pointer);
        float *data = &(*m)(0, 0);
        delete (m);
        delete (data);
    }
}
#endif //KEIGEN_TEMPLATEMATRIX_H
