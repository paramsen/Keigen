#include <jni.h>
#include "TemplateMatrix.h"

extern "C" {
JNIEXPORT void JNICALL
Java_com_paramsen_keigen_KeigenNativeBridgeShared_dispose(JNIEnv *env, jclass jThis, jlong pointer) {
    Keigen::dispose<float>(pointer);
}
}
