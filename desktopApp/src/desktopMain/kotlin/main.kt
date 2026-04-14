import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.foss.appdock.shared.DesktopMainView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

fun main() = application {
    Window(
            onCloseRequest = ::exitApplication,
            title = "App Dock",
            icon = painterResource("icon.png")
    ) { DesktopMainView(window) }
}
