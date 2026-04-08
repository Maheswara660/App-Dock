import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.foss.appdock.shared.DesktopMainView
import com.foss.appdock.shared.platform.DesktopWindowManager
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

fun main() = application {
    Window(
            onCloseRequest = ::exitApplication,
            title = "App Dock",
            icon = painterResource("icon.png")
    ) { DesktopMainView() }

    for (appInstance in DesktopWindowManager.activeStandaloneApps) {
        Window(
            onCloseRequest = { DesktopWindowManager.closeStandalone(appInstance) },
            title = "App Dock WebApp",
            icon = painterResource("icon.png")
        ) {
            val state = rememberWebViewState(appInstance.url)
            WebView(
                state = state,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
