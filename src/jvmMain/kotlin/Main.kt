import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kjipo.Config
import com.kjipo.search.TextSearcher
import com.kjipo.ui.SearchModel
import com.kjipo.ui.SearchUi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import javax.swing.JPanel

@Composable
@Preview
fun App() {
//    var text by remember { mutableStateOf("Hello, World!") }
    val config = Config.getConfig("/home/student/workspace/textSearch/search_config.properties")
    val textSearcher = TextSearcher(config)
    val searchModel = SearchModel(textSearcher)


    MaterialTheme {
        SearchUi(searchModel)
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
