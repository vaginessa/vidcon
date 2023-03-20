import androidx.compose.runtime.*
import app.softwork.bootstrapcompose.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable


fun main() {
    renderComposable(rootElementId = "root") {
        Body()
    }
}

@Composable
fun Body() {
    var counter by remember { mutableStateOf(0) }
    Div {
        Text("Clicked: ${counter}")
    }
    Button(
        attrs = {
            onClick { _ ->
                counter++
            }
        }
    ) {
        Text("Click")
    }
    ChecksAndRadiosView()
}

@Composable
fun ChecksAndRadiosView() {
    Container {
        Row {
            Column(size = 6) {
                var checkVal1 by remember { mutableStateOf(false) }
                var checkVal2 by remember { mutableStateOf(true) }
                Card(header = {
                    Text(value = "Checks")
                }) {
                    Checkbox(checkVal1, label = "Default checkbox") {
                        checkVal1 = it
                    }
                    Checkbox(checkVal2, label = "Checked checkbox") {
                        checkVal2 = it
                    }
                }
            }
            Column(size = 6) {
                Card(header = {
                    Text("Disabled Checks")
                }) {
                    Checkbox(checked = false, disabled = true, label = "Disabled checkbox") {}
                    Checkbox(checked = true, disabled = true, label = "Disabled checked checkbox") {}
                }
            }
        }
    }
}