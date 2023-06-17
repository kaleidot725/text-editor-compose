package jp.kaleidot725.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import jp.kaleidot725.sample.ui.composable.Demo
import jp.kaleidot725.sample.ui.theme.DemoText

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Demo(DemoText) }
    }
}
