package snd.komelia.ui.settings.komf

import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_language_english
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_language_japanese
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_language_japanese_romanized
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_language_korean
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_language_korean_romanized
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_language_simplified_chinese
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_language_traditional_chinese
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_language_chinese_romanized
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_language_brazilian_portuguese
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_language_castilian_spanish
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_language_latin_american_spanish

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryEditable
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.Res
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.komf_settings_text_discard
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.komf_settings_text_edit
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.komf_settings_text_save
import org.jetbrains.compose.resources.stringResource
import snd.komelia.ui.common.components.HttpTextField
import snd.komelia.ui.common.components.LabeledEntry
import snd.komelia.ui.common.components.PasswordTextField

@Composable
fun SavableTextField(
    currentValue: String,
    onValueSave: (String) -> Unit,
    label: @Composable () -> Unit,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    valueChangePolicy: (String) -> Boolean = { true },
    useEditButton: Boolean = false,
    isPassword: Boolean = false,
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        var isChanged by remember { mutableStateOf(false) }
        var editable by remember { mutableStateOf(!useEditButton) }
        var textFieldValue by remember(currentValue) { mutableStateOf(currentValue) }

        if (isPassword) {
            PasswordTextField(
                value = textFieldValue,
                enabled = editable,
                onValueChange = {
                    if (valueChangePolicy(it)) {
                        textFieldValue = it
                        isChanged = true
                    }
                },
                supportingText = supportingText,
                isError = isError,
                label = label,
                modifier = Modifier.weight(1f).animateContentSize()
            )
        } else {
            TextField(
                value = textFieldValue,
                enabled = editable,
                onValueChange = {
                    if (valueChangePolicy(it)) {
                        textFieldValue = it
                        isChanged = true
                    }
                },
                label = label,
                supportingText = supportingText,
                isError = isError,
                singleLine = true,
                modifier = Modifier.weight(1f).animateContentSize()
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            if (!editable) {
                ElevatedButton(
                    onClick = {
                        editable = true
                        textFieldValue = ""
                    },
                ) {
                    Text(stringResource(Res.string.komf_settings_text_edit))
                }
            }

            AnimatedVisibility(editable) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ElevatedButton(
                        enabled = useEditButton || isChanged,
                        onClick = {
                            if (useEditButton) editable = false
                            isChanged = false
                            textFieldValue = currentValue
                        },
                    ) {
                        Text(stringResource(Res.string.komf_settings_text_discard))
                    }
                    FilledTonalButton(
                        onClick = {
                            onValueSave(textFieldValue)
                            if (useEditButton) editable = false
                            isChanged = false
                        },
                        enabled = isChanged,
                    ) {
                        Text(stringResource(Res.string.komf_settings_text_save))
                    }
                }
            }

        }
    }
}

@Composable
fun SavableHttpTextField(
    label: String,
    currentValue: String,
    onValueSave: (String) -> Unit,
    confirmationText: String = stringResource(Res.string.komf_settings_text_save),
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        var isChanged by remember { mutableStateOf(false) }
        var textFieldValue by remember(currentValue) { mutableStateOf(currentValue) }
        HttpTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                isChanged = true
            },
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            isError = isError,
            supportingText = supportingText
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ElevatedButton(
                enabled = isChanged,
                onClick = {
                    isChanged = false
                    textFieldValue = currentValue
                },
            ) {
                Text(stringResource(Res.string.komf_settings_text_discard))
            }
            FilledTonalButton(
                onClick = {
                    onValueSave(textFieldValue)
                    isChanged = false
                },
                enabled = isChanged || isError,
            ) {
                Text(confirmationText)
            }
        }
    }

}

@Composable
fun komfLanguageTagsSuggestions() = listOf(
    LabeledEntry("en", stringResource(Res.string.ui_language_english) + " (en)"),
    LabeledEntry("ja", stringResource(Res.string.ui_language_japanese) + " (ja)"),
    LabeledEntry("ja-ro", stringResource(Res.string.ui_language_japanese_romanized) + " (ja-ro)"),
    LabeledEntry("ko", stringResource(Res.string.ui_language_korean) + " (ko)"),
    LabeledEntry("ko-ro", stringResource(Res.string.ui_language_korean_romanized) + " (ko-ro)"),
    LabeledEntry("zh", stringResource(Res.string.ui_language_simplified_chinese) + " (zh)"),
    LabeledEntry("zh-hk", stringResource(Res.string.ui_language_traditional_chinese) + " (zh-hk)"),
    LabeledEntry("zh-ro", stringResource(Res.string.ui_language_chinese_romanized) + " (zh-ro)"),
    LabeledEntry("pt-br", stringResource(Res.string.ui_language_brazilian_portuguese) + " (pt-br)"),
    LabeledEntry("es", stringResource(Res.string.ui_language_castilian_spanish) + " (es)"),
    LabeledEntry("es-la", stringResource(Res.string.ui_language_latin_american_spanish) + " (es-la)"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionField(
    label: String,
    languageValue: String,
    onLanguageValueChange: (String) -> Unit,
    onLanguageValueSave: () -> Unit,
) {
    var isChanged by remember { mutableStateOf(false) }
    val suggestions = komfLanguageTagsSuggestions()
    val suggestedOptions = suggestions.filter { (tag, label) -> label.contains(languageValue, ignoreCase = true) || tag.contains(languageValue, ignoreCase = true) }
    val focusManager = LocalFocusManager.current
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        var isExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            TextField(
                value = languageValue,
                onValueChange = {
                    onLanguageValueChange(it)
                    isChanged = true
                },
                label = { Text(label) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(PrimaryEditable)
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                suggestedOptions.forEach {
                    DropdownMenuItem(
                        text = { Text(it.label) },
                        onClick = {
                            isExpanded = false
                            focusManager.clearFocus()
                            onLanguageValueChange(it.value)
                            onLanguageValueSave()
                            isChanged = false
                        }
                    )
                }

            }
        }
        FilledTonalButton(
            onClick = {
                onLanguageValueSave()
                isChanged = false
            },
            enabled = isChanged,
        ) {
            Text(stringResource(Res.string.komf_settings_text_save))
        }
    }
}
