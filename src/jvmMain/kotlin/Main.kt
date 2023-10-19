import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kjipo.Config
import com.kjipo.search.TextSearcher
import com.kjipo.ui.SearchModel
import com.kjipo.ui.SearchUi

@Composable
@Preview
fun App() {
    val config = Config.getConfig("/home/student/workspace/textSearch/search_config.properties")
    val textSearcher = TextSearcher(config)
    val searchModel = SearchModel(textSearcher)


    MaterialTheme {
        SearchUi(searchModel)

        textSearcher.getDocumentsByDate()
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
