import androidx.compose.runtime.*
import app.softwork.bootstrapcompose.require
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
    require("./custom.scss")
    renderComposable(rootElementId = "root") {
        Body()
    }
}

@Composable
fun Body() {
    var counter by remember { mutableStateOf(0) }
    Div {
        Text("Clicked: $counter")
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
    Navbar(listOf(Pair("name", "Name"))) {}
}