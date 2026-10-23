package snd.komelia.ui.settings.imagereader.onnxruntime

import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_installation_error

import org.jetbrains.compose.resources.stringResource
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.Res
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.komf_identify_confirm
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_api_for_cross_platform_efficient_gpu_access_using_system_s_underlying_vulkan_metal_or_direct3d_12_te
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_app_restart_is_required_for_changes_to_take_effect
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_choose_onnx_runtime_version
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_cuda_nvidia_gpus_requires_cuda12_and_cudnn9_system_install
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_directml_any_gpu
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_download_cuda12
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_download_cudnn9
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_download_tensorrt
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_downloading_onnx_runtime
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_high_performance_hardware_accelerated_directx_12_library_for_machine_learning
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_install
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_onnx_runtime_support_is_experimental_might_cause_crashes_or_other_app_instabilities
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_rocm_amd_gpus_requires_rocm7_system_install
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_tensorrt_nvidia_gpus_requires_cuda12_cudnn9_and_tensorrt_system_install
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_uses_tensorrt_to_create_optimized_graph_engine_takes_a_significant_time_on_model_first_load_after_in
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_webgpu_any_gpu
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.user_add_cancel

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration.Companion.Underline
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import snd.komelia.onnxruntime.OnnxRuntimeExecutionProvider
import snd.komelia.onnxruntime.OnnxRuntimeExecutionProvider.CPU
import snd.komelia.onnxruntime.OnnxRuntimeExecutionProvider.CUDA
import snd.komelia.onnxruntime.OnnxRuntimeExecutionProvider.DirectML
import snd.komelia.onnxruntime.OnnxRuntimeExecutionProvider.ROCm
import snd.komelia.onnxruntime.OnnxRuntimeExecutionProvider.TENSOR_RT
import snd.komelia.onnxruntime.OnnxRuntimeExecutionProvider.WEBGPU
import snd.komelia.ui.common.components.CheckboxWithLabel
import snd.komelia.ui.dialogs.AppDialog
import snd.komelia.ui.platform.cursorForHand
import snd.komelia.updates.UpdateProgress

@Composable
fun OrtInstallDialog(
    show: Boolean,
    onInstallRequest: (OnnxRuntimeExecutionProvider) -> Flow<UpdateProgress>,
    onDismiss: () -> Unit,
) {
    if (!show) return

    val coroutineScope = rememberCoroutineScope()
    var provider by remember { mutableStateOf<OnnxRuntimeExecutionProvider?>(null) }
    var updateProgress by remember { mutableStateOf<UpdateProgress?>(null) }

    var installError by remember { mutableStateOf<String?>(null) }
    var showPostInstallDialog by remember { mutableStateOf(false) }
    fun onInstall(provider: OnnxRuntimeExecutionProvider) {
        coroutineScope.launch {
            runCatching {
                onInstallRequest(provider)
                    .conflate()
                    .collect {
                        updateProgress = it
                        delay(100)
                    }
            }.onFailure { installError = "${it::class.simpleName} ${it.message}" }
            showPostInstallDialog = true
        }
    }

    if (!showPostInstallDialog) {
        AppDialog(
            modifier = Modifier.widthIn(max = 840.dp),
            header = {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (updateProgress == null)
                        Text(stringResource(Res.string.ui_choose_onnx_runtime_version), style = MaterialTheme.typography.titleLarge)
                    else
                        Text(stringResource(Res.string.ui_downloading_onnx_runtime), style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider(Modifier.padding(top = 10.dp))
                }
            },
            content = {
                val progress = updateProgress
                if (progress != null) {
                    UpdateProgressContent(progress)
                } else {
                    OrtDownloadDialogContent(
                        chosenProvider = provider,
                        onProviderChoice = { provider = it },
                    )
                }
            },
            controlButtons = {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.cursorForHand(),
                        content = { Text(stringResource(Res.string.user_add_cancel)) }
                    )

                    FilledTonalButton(
                        enabled = provider != null && updateProgress == null,
                        onClick = { provider?.let { onInstall(it) } },
                        modifier = Modifier.cursorForHand(),
                    ) {
                        Text(stringResource(Res.string.ui_install))
                    }
                }
            },
            onDismissRequest = { if (updateProgress == null) onDismiss() }
        )
    } else {
        RestartDialog(error = installError, onConfirm = { onDismiss() })
    }
}

@Composable
private fun OrtDownloadDialogContent(
    chosenProvider: OnnxRuntimeExecutionProvider?,
    onProviderChoice: (OnnxRuntimeExecutionProvider) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            Modifier.border(Dp.Hairline, MaterialTheme.colorScheme.tertiary)
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(
                stringResource(Res.string.ui_onnx_runtime_support_is_experimental_might_cause_crashes_or_other_app_instabilities),
                color = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
        val uriHandler = LocalUriHandler.current
        val providers = remember { supportedOnnxRuntimeExecutionProviders() }
        providers.forEach { provider ->
            when (provider) {
                CUDA ->
                    CheckboxWithLabel(
                        checked = chosenProvider == CUDA,
                        onCheckedChange = { onProviderChoice(CUDA) },
                        labelAlignment = Alignment.Top,
                        label = {
                            Column {
                                Text(stringResource(Res.string.ui_cuda_nvidia_gpus_requires_cuda12_and_cudnn9_system_install))
                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        stringResource(Res.string.ui_download_cuda12),
                                        color = MaterialTheme.colorScheme.secondary,
                                        textDecoration = Underline,
                                        modifier = Modifier
                                            .clickable { uriHandler.openUri("https://developer.nvidia.com/cuda-downloads") }
                                            .cursorForHand()
                                            .padding(horizontal = 5.dp),
                                    )
                                    Text(
                                        stringResource(Res.string.ui_download_cudnn9),
                                        color = MaterialTheme.colorScheme.secondary,
                                        textDecoration = Underline,
                                        modifier = Modifier
                                            .clickable { uriHandler.openUri("https://developer.nvidia.com/cudnn-downloads") }
                                            .cursorForHand()
                                            .padding(horizontal = 5.dp),
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                TENSOR_RT -> CheckboxWithLabel(
                    checked = chosenProvider == TENSOR_RT,
                    onCheckedChange = { onProviderChoice(TENSOR_RT) },
                    labelAlignment = Alignment.Top,
                    label = {
                        Column {
                            Text(stringResource(Res.string.ui_tensorrt_nvidia_gpus_requires_cuda12_cudnn9_and_tensorrt_system_install))
                            Text(
                                stringResource(Res.string.ui_uses_tensorrt_to_create_optimized_graph_engine_takes_a_significant_time_on_model_first_load_after_in),
                                style = MaterialTheme.typography.labelLarge
                            )
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    stringResource(Res.string.ui_download_cuda12),
                                    color = MaterialTheme.colorScheme.secondary,
                                    textDecoration = Underline,
                                    modifier = Modifier
                                        .clickable { uriHandler.openUri("https://developer.nvidia.com/cuda-downloads") }
                                        .cursorForHand()
                                        .padding(horizontal = 5.dp),
                                )
                                Text(
                                    stringResource(Res.string.ui_download_cudnn9),
                                    color = MaterialTheme.colorScheme.secondary,
                                    textDecoration = Underline,
                                    modifier = Modifier
                                        .clickable { uriHandler.openUri("https://developer.nvidia.com/cudnn-downloads") }
                                        .cursorForHand()
                                        .padding(horizontal = 5.dp),
                                )
                                Text(
                                    stringResource(Res.string.ui_download_tensorrt),
                                    color = MaterialTheme.colorScheme.secondary,
                                    textDecoration = Underline,
                                    modifier = Modifier
                                        .clickable { uriHandler.openUri("https://developer.nvidia.com/tensorrt") }
                                        .cursorForHand()
                                        .padding(horizontal = 5.dp),
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                ROCm -> CheckboxWithLabel(
                    checked = chosenProvider == ROCm,
                    onCheckedChange = { onProviderChoice(ROCm) },
                    label = { Text(stringResource(Res.string.ui_rocm_amd_gpus_requires_rocm7_system_install)) },
                    modifier = Modifier.fillMaxWidth()
                )

                DirectML -> CheckboxWithLabel(
                    checked = chosenProvider == DirectML,
                    onCheckedChange = { onProviderChoice(DirectML) },
                    labelAlignment = Alignment.Top,
                    label = {
                        Column {
                            Text(stringResource(Res.string.ui_directml_any_gpu))
                            Text(
                                stringResource(Res.string.ui_high_performance_hardware_accelerated_directx_12_library_for_machine_learning),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                CPU -> CheckboxWithLabel(
                    checked = chosenProvider == CPU,
                    onCheckedChange = { onProviderChoice(CPU) },
                    label = { Text("CPU") },
                    modifier = Modifier.fillMaxWidth()
                )

                WEBGPU -> CheckboxWithLabel(
                    checked = chosenProvider == WEBGPU,
                    onCheckedChange = { onProviderChoice(WEBGPU) },
                    labelAlignment = Alignment.Top,
                    label = {
                        Column {
                            Text(stringResource(Res.string.ui_webgpu_any_gpu))
                            Text(
                                stringResource(Res.string.ui_api_for_cross_platform_efficient_gpu_access_using_system_s_underlying_vulkan_metal_or_direct3d_12_te),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun RestartDialog(
    error: String?,
    onConfirm: () -> Unit,
) {
    AppDialog(
        modifier = Modifier.widthIn(max = 600.dp),
        content = {
            Box(Modifier.padding(30.dp)) {
                if (error != null)
                    Text(stringResource(Res.string.ui_installation_error, error ?: ""))
                else
                    Text(stringResource(Res.string.ui_app_restart_is_required_for_changes_to_take_effect))
            }
        },
        controlButtons = {
            Box(modifier = Modifier.padding(bottom = 10.dp, end = 10.dp)) {
                FilledTonalButton(
                    onClick = onConfirm,
                    modifier = Modifier.cursorForHand(),
                ) {
                    Text(stringResource(Res.string.komf_identify_confirm))
                }
            }
        },
        onDismissRequest = { }
    )
}
