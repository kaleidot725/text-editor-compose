[![](https://jitpack.io/v/kaleidot725/text-editor-compose.svg)](https://jitpack.io/#kaleidot725/text-editor-compose)
[![Android API](https://img.shields.io/badge/api-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![kotlin](https://img.shields.io/github/languages/top/kaleidot725/text-editor-compose)](https://kotlinlang.org/)
![License MIT](https://img.shields.io/github/license/kaleidot725/text-editor-compose)

<h1 align="center">
    Text Editor Compose
</h1>

<h3 align="center">
    A simple text editor for Jetpack Compose
</h3>

<h3 align="center">
    <img align="center" width=400 src="https://github.com/kaleidot725/text-editor-compose/blob/main/demo.gif">
</h3>

## Features

- [x] Edit multiple line text
- [x] Insert and delete newline
- [x] Get selected line index
- [x] Display line number
- [ ] Select mutilple line
- [ ] Support physical keyboard

## Usage

This library is easy to use, just follow the steps below to add a dependency and write codes.

### Step 1: Add the JitPack repository to build.gradle

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### Step 2: Add the library to the dependencies

```groovy
dependencies {
	implementation 'com.github.kaleidot725:text-editor-compose:0.1.0'
}
```

### Step 3: Declare TextEditor & TextEditorState

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme {
                val textEditorState by rememberTextEditorState(lines = DemoText.lines())
                TextEditor(
                    textEditorState = textEditorState, 
                    onUpdatedState = { },              
                    modifier = Modifier.fillMaxSize() 
                )
            }
        }
    }
}
```

### Step 4: Customize what each row displays

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme {
                val textEditorState by rememberTextEditorState(lines = DemoText.lines())
                TextEditor(
                    textEditorState = textEditorState,
                    onUpdatedState = { },
                    modifier = Modifier.fillMaxSize(),
                    decorationBox = { index, isSelected, innerTextField ->
                        val backgroundColor = if (isSelected) Color(0x8000ff00) else Color.White           
　　　　　　　　　　　　　　 Row(modifier = Modifier.background(backgroundColor)) {
                            Text(text = (index + 1).toString().padStart(3, '0'))
                            Spacer(modifier = Modifier.width(4.dp))
                            innerTextField(modifier = Modifier.fillMaxWidth())
                        }
                    }
                )
            }
        }
    }
}
```
