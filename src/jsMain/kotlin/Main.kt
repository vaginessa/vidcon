import androidx.compose.runtime.*
import app.softwork.bootstrapcompose.*
import io.github.jsixface.client.AppContainer
import io.github.jsixface.client.MainApp
import kotlinx.coroutines.MainScope
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

val scope = MainScope()
fun main() {
    renderComposable(rootElementId = "root") {
        MainApp(AppContainer())
    }
}

@Composable
fun Body() {

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
