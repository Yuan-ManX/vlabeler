@file:OptIn(ExperimentalFoundationApi::class)

package com.sdercolin.vlabeler.ui.starter

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sdercolin.vlabeler.model.LabelerConf
import com.sdercolin.vlabeler.model.Plugin
import com.sdercolin.vlabeler.ui.AppRecordStore
import com.sdercolin.vlabeler.ui.AppState
import com.sdercolin.vlabeler.ui.common.CircularProgress
import com.sdercolin.vlabeler.ui.common.ConfirmButton
import com.sdercolin.vlabeler.ui.common.Tooltip
import com.sdercolin.vlabeler.ui.common.WarningTextStyle
import com.sdercolin.vlabeler.ui.dialog.OpenFileDialog
import com.sdercolin.vlabeler.ui.dialog.WarningDialog
import com.sdercolin.vlabeler.ui.dialog.plugin.LabelerPluginDialog
import com.sdercolin.vlabeler.ui.dialog.plugin.TemplatePluginDialog
import com.sdercolin.vlabeler.ui.string.Strings
import com.sdercolin.vlabeler.ui.string.string
import com.sdercolin.vlabeler.ui.theme.DarkYellow
import com.sdercolin.vlabeler.ui.theme.getSwitchColors
import kotlinx.coroutines.CoroutineScope

@Composable
fun ProjectCreator(
    appState: AppState,
    onCreateListener: ProjectCreatorState.OnCreateListener,
    cancel: () -> Unit,
    activeLabelerConfs: List<LabelerConf>,
    activeTemplatePlugins: List<Plugin>,
    appRecordStore: AppRecordStore,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    state: ProjectCreatorState = rememberProjectCreatorState(
        appState,
        coroutineScope,
        activeLabelerConfs,
        appRecordStore,
    ),
) {
    val scrollState = rememberScrollState()

    Surface(Modifier.fillMaxSize()) {
        Box {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 60.dp, vertical = 40.dp)
                        .verticalScroll(scrollState),
                ) {
                    Text(
                        text = string(Strings.StarterNewProjectTitle),
                        style = MaterialTheme.typography.h4,
                        maxLines = 1,
                    )
                    Spacer(Modifier.height(25.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        SampleDirectoryTextField(state)
                        WorkingDirectoryTextField(state)
                        ProjectNameTextField(state)
                        CacheDirectoryTextField(state)
                        LabelerSelectorRow(state, activeLabelerConfs, activeTemplatePlugins)
                        InputFileTextField(state)
                        OtherSettingsRow(state)
                    }

                    Spacer(Modifier.height(30.dp))
                    ButtonBar(cancel, state, onCreateListener)
                }
                VerticalScrollbar(rememberScrollbarAdapter(scrollState), Modifier.width(15.dp))
            }
            if (state.isLoading) {
                CircularProgress()
            }
            state.currentPathPicker?.let { picker ->
                PickerDialog(state, picker)
            }
            state.warningText?.let {
                WarningDialog(string(it), finish = { state.warningText = null }, style = WarningTextStyle.Warning)
            }
        }
    }
}

@Composable
private fun SampleDirectoryTextField(state: ProjectCreatorState) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.sampleDirectory,
        onValueChange = state::updateSampleDirectory,
        label = { Text(string(Strings.StarterNewSampleDirectory)) },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { state.pickSampleDirectory() }) {
                Icon(Icons.Default.FolderOpen, null)
            }
        },
        isError = state.isSampleDirectoryValid().not(),
    )
}

@Composable
private fun WorkingDirectoryTextField(state: ProjectCreatorState) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.workingDirectory,
        onValueChange = state::updateWorkingDirectory,
        label = { Text(string(Strings.StarterNewWorkingDirectory)) },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { state.pickWorkingDirectory() }) {
                Icon(Icons.Default.FolderOpen, null)
            }
        },
        isError = state.isWorkingDirectoryValid().not(),
    )
}

@Composable
private fun ProjectNameTextField(state: ProjectCreatorState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            modifier = Modifier.widthIn(min = 400.dp),
            value = state.projectName,
            onValueChange = state::updateProjectName,
            label = { Text(string(Strings.StarterNewProjectName)) },
            singleLine = true,
            isError = state.isProjectNameValid().not(),
        )
        if (state.isProjectFileExisting()) {
            Spacer(Modifier.width(15.dp))
            TooltipArea(
                tooltip = { Tooltip(string(Strings.StarterNewProjectNameWarning)) },
            ) {
                Icon(Icons.Default.Warning, null, tint = DarkYellow, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun CacheDirectoryTextField(state: ProjectCreatorState) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.cacheDirectory,
        onValueChange = state::updateCacheDirectory,
        label = { Text(string(Strings.StarterNewCacheDirectory)) },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { state.pickCacheDirectory() }) {
                Icon(Icons.Default.FolderOpen, null)
            }
        },
        isError = state.isCacheDirectoryValid().not(),
    )
}

@Composable
private fun LabelerSelectorRow(
    state: ProjectCreatorState,
    availableLabelerConfs: List<LabelerConf>,
    availableTemplatePlugins: List<Plugin>,
) {
    var labelerDialogShown by remember { mutableStateOf(false) }
    var pluginDialogShown by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        LabelerSelector(state, availableLabelerConfs)
        Spacer(Modifier.width(10.dp))
        IconButton(onClick = { labelerDialogShown = true }) {
            val color = if (state.labelerError) {
                MaterialTheme.colors.error
            } else {
                LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
            }
            Icon(Icons.Default.Settings, null, tint = color)
        }
        Spacer(Modifier.width(60.dp))
        TemplatePluginSelector(state, availableTemplatePlugins)
        Spacer(Modifier.width(10.dp))
        IconButton(
            enabled = state.templatePlugin != null,
            onClick = { pluginDialogShown = true },
        ) {
            val color = if (state.templatePluginError) {
                MaterialTheme.colors.error
            } else {
                LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
            }
            Icon(Icons.Default.Settings, null, tint = color)
        }
    }
    if (labelerDialogShown) {
        val snackbarHostState = remember { SnackbarHostState() }
        LabelerPluginDialog(
            isExistingProject = false,
            appConf = state.appConf,
            appRecordStore = state.appRecordStore,
            snackbarHostState = snackbarHostState,
            labeler = requireNotNull(state.labeler),
            paramMap = requireNotNull(state.labelerParams),
            savedParamMap = requireNotNull(state.labelerSavedParams),
            submit = {
                if (it != null) state.updateLabelerParams(it)
                labelerDialogShown = false
            },
            save = { state.saveLabelerParams(it) },
            load = { state.updateLabelerParams(it) },
        )
    }
    if (pluginDialogShown) {
        val snackbarHostState = remember { SnackbarHostState() }
        TemplatePluginDialog(
            appConf = state.appConf,
            appRecordStore = state.appRecordStore,
            snackbarHostState = snackbarHostState,
            plugin = requireNotNull(state.templatePlugin),
            paramMap = requireNotNull(state.templatePluginParams),
            savedParamMap = requireNotNull(state.templatePluginSavedParams),
            submit = {
                if (it != null) state.updatePluginParams(it)
                pluginDialogShown = false
            },
            load = { state.updatePluginParams(it) },
            save = { state.savePluginParams(it) },
        )
    }
}

@Composable
private fun LabelerSelector(
    state: ProjectCreatorState,
    availableLabelerConfs: List<LabelerConf>,
) {
    LaunchedEffect(Unit) {
        state.updateLabeler(state.labeler)
    }
    Box {
        var expanded by remember { mutableStateOf(false) }
        TextField(
            modifier = Modifier.widthIn(min = 250.dp),
            value = state.labeler.displayedName.get(),
            onValueChange = { },
            readOnly = true,
            label = { Text(string(Strings.StarterNewLabeler)) },
            singleLine = true,
            leadingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ExpandMore, null)
                }
            },
        )
        DropdownMenu(
            modifier = Modifier.align(Alignment.CenterEnd),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            availableLabelerConfs.forEach { conf ->
                DropdownMenuItem(
                    onClick = {
                        state.updateLabeler(conf)
                        expanded = false
                    },
                ) {
                    Text(text = conf.displayedName.get())
                }
            }
        }
    }
}

@Composable
private fun TemplatePluginSelector(
    state: ProjectCreatorState,
    availableTemplatePlugins: List<Plugin>,
) {
    Box {
        var expanded by remember { mutableStateOf(false) }
        TextField(
            modifier = Modifier.widthIn(min = 250.dp),
            value = state.getTemplateName(),
            onValueChange = { },
            readOnly = true,
            label = { Text(string(Strings.StarterNewTemplatePlugin)) },
            singleLine = true,
            leadingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ExpandMore, null)
                }
            },
        )
        DropdownMenu(
            modifier = Modifier.align(Alignment.CenterEnd),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                onClick = {
                    state.updatePlugin(null)
                    expanded = false
                },
            ) {
                Text(text = string(Strings.StarterNewTemplatePluginNone))
            }
            state.getSupportedPlugins(availableTemplatePlugins).forEach { plugin ->
                DropdownMenuItem(
                    onClick = {
                        state.updatePlugin(plugin)
                        expanded = false
                    },
                ) {
                    Text(text = plugin.displayedName.get())
                }
            }
        }
    }
}

@Composable
private fun InputFileTextField(state: ProjectCreatorState) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.inputFile,
        onValueChange = { state.updateInputFile(it, editedByUser = true) },
        label = { Text(state.getInputFileLabelText()) },
        placeholder = state.getInputFilePlaceholderText()?.let { { Text(it) } },
        enabled = state.isInputFileEnabled(),
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { state.pickInputFile() }, enabled = state.isInputFileEnabled()) {
                Icon(Icons.Default.FolderOpen, null)
            }
        },
        isError = state.isInputFileValid().not(),
    )
}

@Composable
private fun OtherSettingsRow(state: ProjectCreatorState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        EncodingSelector(state)
        Spacer(Modifier.width(60.dp))
        AutoExportSwitch(state)
    }
}

@Composable
private fun EncodingSelector(state: ProjectCreatorState) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextField(
            modifier = Modifier.widthIn(min = 200.dp),
            value = state.encoding,
            onValueChange = { },
            enabled = state.isEncodingSelectionEnabled,
            readOnly = true,
            label = { Text(string(Strings.StarterNewEncoding)) },
            singleLine = true,
            leadingIcon = {
                IconButton(onClick = { expanded = true }, enabled = state.isEncodingSelectionEnabled) {
                    Icon(Icons.Default.ExpandMore, null)
                }
            },
        )
        DropdownMenu(
            modifier = Modifier.align(Alignment.CenterEnd),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            state.encodings.forEach { encodingName ->
                DropdownMenuItem(
                    onClick = {
                        state.encoding = encodingName
                        expanded = false
                    },
                ) {
                    Text(text = encodingName)
                }
            }
        }
    }
}

@Composable
private fun AutoExportSwitch(state: ProjectCreatorState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val contentAlpha = if (state.canAutoExport) {
            LocalContentAlpha.current
        } else {
            ContentAlpha.disabled
        }
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
            Switch(
                checked = state.autoExport,
                onCheckedChange = { state.toggleAutoExport(it) },
                colors = getSwitchColors(),
                enabled = state.canAutoExport,
            )
            Spacer(Modifier.width(10.dp))
            Text(string(Strings.StarterNewAutoExport))
        }
        Spacer(Modifier.width(15.dp))
        TooltipArea(
            tooltip = { Tooltip(string(Strings.StarterNewAutoExportHelp)) },
        ) {
            Icon(
                Icons.Default.HelpOutline,
                null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun ButtonBar(
    cancel: () -> Unit,
    state: ProjectCreatorState,
    onCreateListener: ProjectCreatorState.OnCreateListener,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        OutlinedButton(onClick = cancel) {
            Text(string(Strings.CommonCancel))
        }

        ConfirmButton(
            onClick = { state.create(onCreateListener) },
            enabled = state.isValid(),
        )
    }
}

@Composable
private fun PickerDialog(
    state: ProjectCreatorState,
    picker: PathPicker,
) {
    val title = state.getFilePickerTitle(picker)
    val initial = state.getFilePickerInitialDirectory(picker)
    val extensions = state.getFilePickerExtensions(picker)
    val directoryMode = state.getFilePickerDirectoryMode(picker)
    OpenFileDialog(
        title = title,
        initialDirectory = initial,
        extensions = extensions,
        directoryMode = directoryMode,
    ) { parent, name ->
        state.handleFilePickerResult(picker, parent, name)
    }
}
