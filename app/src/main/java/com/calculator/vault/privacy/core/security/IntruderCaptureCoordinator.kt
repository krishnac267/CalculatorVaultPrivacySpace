package com.calculator.vault.privacy.core.security

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class IntruderCaptureCoordinator @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    @Volatile
    var lifecycleOwner: LifecycleOwner? = null

    @Volatile
    var hasCameraPermission: () -> Boolean = { false }

    private val executor: Executor = ContextCompat.getMainExecutor(context)

    suspend fun capturePhoto(): String? {
        if (!hasCameraPermission()) return null
        val owner = lifecycleOwner ?: return null
        return suspendCancellableCoroutine { cont ->
            val future = ProcessCameraProvider.getInstance(context)
            future.addListener({
                try {
                    val provider = future.get()
                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build()
                    provider.unbindAll()
                    provider.bindToLifecycle(owner, selector, capture)
                    val file = File(context.filesDir, "intruder_${System.currentTimeMillis()}.jpg")
                    val options = ImageCapture.OutputFileOptions.Builder(file).build()
                    capture.takePicture(
                        options,
                        executor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                provider.unbindAll()
                                cont.resume(file.absolutePath)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                provider.unbindAll()
                                cont.resume(null)
                            }
                        },
                    )
                } catch (_: Exception) {
                    cont.resume(null)
                }
            }, executor)
        }
    }
}
