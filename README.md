# Lesser API TEST
Lesser API For Android <br />

## Installation
1. Clone this repository and import into **Android Studio**
```bash
git clone git@github.com:Artificial-Society/lesser-api-test.git
```

2. Download AAR File
3. Copy and paste the downloaded file into the following directory:
```bash
../LesserAPITest/app/libs/
```

## How To Use
1. With CameraX Library
- Just run the code and it'll work.

2. Without CameraX Library (e.g. Camera2 ..)
- Use commented-out code
    - Comment-out 23-26 lines (using CameraX)
    - Uncomment 30-42 lines (using Bitmap)
    - Obtain bitmap from your camera library and Just replace `bitmap` in `GazeBitmapAnalyzer(this~).bitmapAnalyze(bitmap)`
    