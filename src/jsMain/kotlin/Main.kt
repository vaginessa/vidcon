import io.github.jsixface.client.AppContainer
import io.github.jsixface.client.MainApp
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        MainApp(AppContainer())
    }
}
