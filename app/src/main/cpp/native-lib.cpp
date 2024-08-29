#include <jni.h>

extern "C" {
    JNIEXPORT void JNICALL
    Java_com_project_1aurora_emu_NativeCode_surfaceChanged(JNIEnv *env, jobject obj, jobject surface, jint width, jint height) {
        // TODO: init compositor
    }
}