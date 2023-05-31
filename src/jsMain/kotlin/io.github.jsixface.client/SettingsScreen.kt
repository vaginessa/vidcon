package io.github.jsixface.client

import androidx.compose.runtime.*
import app.softwork.bootstrapcompose.*
import io.github.jsixface.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.dom.*
import scope


@Composable
fun ShowSettings(viewModel: SettingsViewModel) {

    val location = remember { mutableStateListOf("") }
    val extensions = remember { mutableStateListOf("") }
    var workLocation by remember { mutableStateOf("") }

    scope.launch {
        viewModel.settings.collect { s ->
            s?.let {
                location.clear()
                extensions.clear()
                location.addAll(s.libraryLocations)
                extensions.addAll(s.videoExtensions)
                workLocation = s.workspaceLocation
            }
        }
    }

    Container {
        Row {
            Column(size = 10, attrs = { classes("offset-md-1") }) {
                H2(attrs = { classes("text-center", "my-3") }) { Text("Settings") }
                Form(attrs = { onSubmit { it.preventDefault() } }) {
                    Row {
                        Column(size = 3) { H3 { Text("Media Locations") } }
                        Column(size = 9) {
                            TextListGroup(list = location,
                                    onAdd = { location.add(it) },
                                    onDelete = { location.remove(it) })
                        }
                    }
                    Row {
                        Column(size = 3) { H3 { Text("Video extensions") } }
                        Column(size = 9) {
                            TextListGroup(list = extensions,
                                    onAdd = { extensions.add(it) },
                                    onDelete = { extensions.remove(it) })
                        }
                    }
                    Row {
                        Column(size = 3) { H3 { Text("Work Location") } }
                        Column(size = 9) {
                            InputGroup {
                                TextInput(value = workLocation, placeholder = "Work Location") {
                                    workLocation = it.value
                                }
                            }
                        }
                    }
                    // Save button
                    Row(attrs = { classes("mt-4") }) {
                        Column(size = 4) {}
                        Column(size = 4) {
                            Button(
                                    title = "Save Settings",
                                    attrs = { classes("btn-success", "btn-lg") }) {
                                viewModel.save(locations = location, extension = extensions, workLocation = workLocation)
                            }
                        }
                        Column(size = 4) {}
                    }
                }
            }
        }
    }
}

@Composable
fun TextListGroup(list: List<String>, onAdd: (String) -> Unit, onDelete: (String) -> Unit) {
    var newItem by mutableStateOf("")

    FormGroup {
        list.forEach { loc ->
            Row(attrs = { classes("my-1") }) {
                var disabled by mutableStateOf(true)
                Column(size = 8) {
                    InputGroup {
                        TextInput(
                                value = loc,
                                placeholder = "Location",
                                disabled = disabled
                        ) {}
                    }
                }
                Column(size = 4) {
                    Button(title = "Edit", attrs = { classes("me-2") }) { disabled = false }
                    Button(title = "Delete", attrs = { classes("me-2") }) { onDelete(loc) }
                }
            }
        }
        Row {
            Column(size = 8) {
                InputGroup {
                    TextInput(value = newItem, placeholder = "Location") { newItem = it.value }
                }
            }
            Column(size = 4) {
                Button(title = "Add") {
                    onAdd(newItem)
                    newItem = ""
                }
            }
        }
        Hr(attrs = { classes("my-4") })
    }
}