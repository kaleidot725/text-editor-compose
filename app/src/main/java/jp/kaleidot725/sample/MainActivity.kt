package jp.kaleidot725.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jp.kaleidot725.sample.ui.theme.SampleTheme
import jp.kaleidot725.texteditor.extension.rememberTextEditorState
import jp.kaleidot725.texteditor.view.TextEditor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme {
                var textEditorState by rememberTextEditorState(lines = DemoText.lines())
                TextEditor(
                    textEditorState = textEditorState,
                    onUpdatedState = { textEditorState = it },
                    modifier = Modifier.fillMaxSize()
                ) { index, isSelected, innerTextField ->
                    val color =if (isSelected) Color.Green else Color.White
                    Row(modifier = Modifier.background(color)) {
                        Text(text = index.toString().padEnd(3, '0'))
                        Spacer(modifier = Modifier.width(4.dp))
                        innerTextField(modifier = Modifier)
                    }
                }
            }
        }
    }
}

private val DemoText = """
    アリスは川辺でおねえさんのよこにすわって、
    なんにもすることがないのでとても退屈（たいくつ）しはじめていました。
    一、二回はおねえさんの読んでいる本をのぞいてみたけれど、そこには絵も会話もないのです。
    「絵や会話のない本なんて、なんの役にもたたないじゃないの」とアリスは思いました。
    そこでアリスは、頭のなかで、ひなぎくのくさりをつくったら楽しいだろうけれど、
    起きあがってひなぎくをつむのもめんどくさいし、どうしようかと考えていました
    （といっても、昼間で暑いし、とってもねむくて頭もまわらなかったので、これもたいへんだったのですが）。
    そこへいきなり、ピンクの目をした白うさぎが近くを走ってきたのです。
    それだけなら、そんなにめずらしいことでもありませんでした。さらにアリスとしては、
    そのうさぎが「どうしよう！　どうしよう！　ちこくしちゃうぞ！」とつぶやくのを聞いたときも、
    それがそんなにへんてこだとは思いませんでした（あとから考えてみたら、これも不思議に思うべきだったのですけれど、
    でもこのときには、それがごく自然なことに思えたのです）。
    アリスは川辺でおねえさんのよこにすわって、
    なんにもすることがないのでとても退屈（たいくつ）しはじめていました。
    一、二回はおねえさんの読んでいる本をのぞいてみたけれど、そこには絵も会話もないのです。
    「絵や会話のない本なんて、なんの役にもたたないじゃないの」とアリスは思いました。
    そこでアリスは、頭のなかで、ひなぎくのくさりをつくったら楽しいだろうけれど、
    起きあがってひなぎくをつむのもめんどくさいし、どうしようかと考えていました
    （といっても、昼間で暑いし、とってもねむくて頭もまわらなかったので、これもたいへんだったのですが）。
    そこへいきなり、ピンクの目をした白うさぎが近くを走ってきたのです。
    それだけなら、そんなにめずらしいことでもありませんでした。さらにアリスとしては、
    そのうさぎが「どうしよう！　どうしよう！　ちこくしちゃうぞ！」とつぶやくのを聞いたときも、
    それがそんなにへんてこだとは思いませんでした（あとから考えてみたら、これも不思議に思うべきだったのですけれど、
    でもこのときには、それがごく自然なことに思えたのです）。
    アリスは川辺でおねえさんのよこにすわって、
    なんにもすることがないのでとても退屈（たいくつ）しはじめていました。
    一、二回はおねえさんの読んでいる本をのぞいてみたけれど、そこには絵も会話もないのです。
    「絵や会話のない本なんて、なんの役にもたたないじゃないの」とアリスは思いました。
    そこでアリスは、頭のなかで、ひなぎくのくさりをつくったら楽しいだろうけれど、
    起きあがってひなぎくをつむのもめんどくさいし、どうしようかと考えていました
    （といっても、昼間で暑いし、とってもねむくて頭もまわらなかったので、これもたいへんだったのですが）。
    そこへいきなり、ピンクの目をした白うさぎが近くを走ってきたのです。
    それだけなら、そんなにめずらしいことでもありませんでした。さらにアリスとしては、
    そのうさぎが「どうしよう！　どうしよう！　ちこくしちゃうぞ！」とつぶやくのを聞いたときも、
    それがそんなにへんてこだとは思いませんでした（あとから考えてみたら、これも不思議に思うべきだったのですけれど、
    でもこのときには、それがごく自然なことに思えたのです）。
    アリスは川辺でおねえさんのよこにすわって、
    なんにもすることがないのでとても退屈（たいくつ）しはじめていました。
    一、二回はおねえさんの読んでいる本をのぞいてみたけれど、そこには絵も会話もないのです。
    「絵や会話のない本なんて、なんの役にもたたないじゃないの」とアリスは思いました。
    そこでアリスは、頭のなかで、ひなぎくのくさりをつくったら楽しいだろうけれど、
    起きあがってひなぎくをつむのもめんどくさいし、どうしようかと考えていました
    （といっても、昼間で暑いし、とってもねむくて頭もまわらなかったので、これもたいへんだったのですが）。
    そこへいきなり、ピンクの目をした白うさぎが近くを走ってきたのです。
    それだけなら、そんなにめずらしいことでもありませんでした。さらにアリスとしては、
    そのうさぎが「どうしよう！　どうしよう！　ちこくしちゃうぞ！」とつぶやくのを聞いたときも、
    それがそんなにへんてこだとは思いませんでした（あとから考えてみたら、これも不思議に思うべきだったのですけれど、
    でもこのときには、それがごく自然なことに思えたのです）。
""".trimIndent()
