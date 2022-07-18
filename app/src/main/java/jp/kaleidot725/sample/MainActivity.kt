package jp.kaleidot725.sample

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import jp.kaleidot725.sample.ui.theme.SampleTheme
import jp.kaleidot725.texteditor.controller.TextEditorController
import jp.kaleidot725.texteditor.extension.rememberTextEditorController
import jp.kaleidot725.texteditor.view.TextEditor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SampleTheme {
                val textEditorState by rememberTextEditorController(
                    lines = DemoText.lines(),
                    onChanged = { /** SAVE ACTION */ })
                Column {
                    TextEditorMenu(textEditorController = textEditorState)
                    TextEditor(
                        textEditorController = textEditorState,
                        modifier = Modifier.fillMaxSize()
                    ) { index, isSelected, innerTextField ->
                        val backgroundColor = if (isSelected) Color(0x8000ff00) else Color.White
                        Row(modifier = Modifier.background(backgroundColor)) {
                            Text(text = (index + 1).toString().padStart(3, '0'))
                            Spacer(modifier = Modifier.width(4.dp))
                            innerTextField(modifier = Modifier.weight(0.9f, fill = true))
                        }
                    }
                }
            }
        }
    }
}

private val DemoText = """
Android Inc. was founded in Palo Alto, California, 
in October 2003 by Andy Rubin, Rich Miner, Nick Sears, 
and Chris White.[20][21] Rubin described the Android project as having "tremendous potential in 
developing smarter mobile devices that are more aware of its owner's location and preferences".[21] 
The early intentions of the company were to develop an advanced operating system for digital cameras, 
and this was the basis of its pitch to investors in April 2004.[22] 
The company then decided that the market for cameras was not large enough for its goals, 
and five months later it had diverted its efforts and was pitching Android as a handset operating system that would rival Symbian and Microsoft Windows Mobile.[22][23]
Rubin had difficulty attracting investors early on, and Android was facing eviction from its office space. 
Steve Perlman, a close friend of Rubin, brought him ${'$'}10,000 in cash in an envelope, 
and shortly thereafter wired an undisclosed amount as seed funding. 
Perlman refused a stake in the company, and has stated "I did it because I believed in the thing, and I wanted to help Andy."[24][25]

In 2005, Rubin tried to negotiate deals with Samsung[26] and HTC.[27] Shortly afterwards, 
Google acquired the company in July of that year for at least ${'$'}50 million;[21][28] 
this was Google's "best deal ever" according to Google's then-vice president of corporate development, 
David Lawee, in 2010.[26] Android's key employees, including Rubin, Miner, Sears, and White, 
joined Google as part of the acquisition.[21] Not much was known about the secretive Android Inc. at the time, 
with the company having provided few details other than that it was making software for mobile phones.[21] 
At Google, the team led by Rubin developed a mobile device platform powered by the Linux kernel. 
Google marketed the platform to handset makers and carriers on the promise of providing a flexible, upgradeable system.[29] 
Google had "lined up a series of hardware components and software partners and signaled to carriers that it was open to various degrees of cooperation".[attribution needed][30]

Speculation about Google's intention to enter the mobile communications market continued to build through December 2006.
[31] An early prototype had a close resemblance to a BlackBerry phone, with no touchscreen and a physical QWERTY keyboard, 
but the arrival of 2007's Apple iPhone meant that Android "had to go back to the drawing board".[32][33] 
Google later changed its Android specification documents to state that "Touchscreens will be supported", 
although "the Product was designed with the presence of discrete physical buttons as an assumption, 
therefore a touchscreen cannot completely replace physical buttons".[34] By 2008, 
both Nokia and BlackBerry announced touch-based smartphones to rival the iPhone 3G, and Android's focus eventually switched to just touchscreens. 
The first commercially available smartphone running Android was the HTC Dream, also known as T-Mobile G1, announced on September 23, 2008.[35][36]
""".trimIndent()

@Composable
private fun ColumnScope.TextEditorMenu(textEditorController: TextEditorController) {
    val context: Context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Row(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "Enable multiple selection mode",
            modifier = Modifier
                .weight(0.9f, true)
                .align(Alignment.CenterVertically)
        )
        Switch(
            checked = textEditorController.isMultipleSelectionMode.value,
            onCheckedChange = {
                textEditorController.setMultipleSelectionMode(
                    !textEditorController.isMultipleSelectionMode.value
                )
            }
        )
    }

    Row(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "Copy selected lines",
            modifier = Modifier
                .weight(0.9f, true)
                .align(Alignment.CenterVertically)
        )
        Button(
            onClick = {
                val text = textEditorController.getSelectedText()
                textEditorController.setMultipleSelectionMode(false)

                clipboardManager.setText(AnnotatedString(text))
                Toast.makeText(context, "Copy selected text to clipboard", Toast.LENGTH_SHORT).show()
            }
        ) {
            Text(text = "EXECUTE")
        }
    }

    Row(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "Delete selected lines",
            modifier = Modifier
                .weight(0.9f, true)
                .align(Alignment.CenterVertically)
        )
        Button(onClick = {
            textEditorController.deleteSelectedLines()
            textEditorController.setMultipleSelectionMode(false)
        }) {
            Text(text = "EXECUTE")
        }
    }
}