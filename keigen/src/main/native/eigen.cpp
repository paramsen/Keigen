#include <jni.h>
#include "Eigen/Eigen/Dense"
#include <android/log.h>

using Eigen::MatrixXf;
using Eigen::Map;
using Eigen::Matrix;

extern "C" {
    JNIEXPORT jlong JNICALL
    Java_com_paramsen_keigen_KeigenNativeBridge_initialize(JNIEnv *env, jclass jThis, jint rows, jint cols, jfloat fill) {
        int a[] = {1,2,3,4,5,6,7,8};
        Map<Matrix<int, 2 , 4>> b(a);
        MatrixXf m(2,2);

        m(0,0) = 3.0F;
        __android_log_print(ANDROID_LOG_DEBUG, "NATIVE", "%d", b(0,1));
        return 0;
    }
}
