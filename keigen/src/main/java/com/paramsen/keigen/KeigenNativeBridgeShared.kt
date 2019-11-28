package com.paramsen.keigen

/**
 * @author Pär Amsen 11/2019
 */
object KeigenNativeBridgeShared {
    private var loaded = false

    fun loadNativeLibrary() {
        synchronized(loaded) {
            if(!loaded) {
                System.loadLibrary("keigen")
                loaded = true
            }
        }
    }

    external fun dispose(nativePointer: Long)
}
