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
- [x] Copy multiple line
- [x] Delete multiple line
- [x] Support physical keyboard

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
	implementation 'com.github.kaleidot725:text-editor-compose:0.3.0'
}
```

### Step 3: Change windowSoftInputMode

**AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
		︙
	>
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.Sample"
            android:windowSoftInputMode="adjustResize" // !! ADD THIS LINE !!
            >
          	︙
        </activity>
    </application>

</manifest>
```

**MainActivity.kt**

```kotlin
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		︙
        WindowCompat.setDecorFitsSystemWindows(window, false) // !! ADD THIS LINE !!
        	︙
    }
}
```

### Step 4: Declare TextEditor & TextEditorState

```kotlin
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SampleTheme {
                var textEditorState by remember { mutableStateOf(TextEditorState.create(DemoText)) }
		val bottomPaddingValue = with(LocalDensity.current) { WindowInsets.ime.getBottom(this).toDp() }
                val contentPaddingValues = PaddingValues(bottom = bottomPaddingValue)
		
                Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                    TextEditor(
                        textEditorState = textEditorState,
                        onChanged = { textEditorState = it },
                        contentPaddingValues = contentPaddingValues,
                    )
                }
            }
        }
    }
}
```

## Demo

### Edit multiple line text

![Edit multiple line text](./docs/1.gif)

### Insert and delete newline

![Insert and delete newline](./docs/2.gif)

### Copy multiple line

![Copy multiple line](./docs/3.gif)

### Delete multiple line

![Delete multiple line](./docs/4.gif)
