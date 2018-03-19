# RecordingBox
## 使用方式
```java
RecordNoticeBox.newBox(context)
                        .setFileDir("mine_record")//设置文件夹名称
                        .setLimitTime(8)//设置录制时长
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .setRecordListener(new RecordListener() {
                            @Override
                            public void finish(String filePath, long time) {
                            //filePath---录制音频文件路径，time---录制音频时长
                            }

                            @Override
                            public void cancle() {
                            }
                        }).create()
                        .show();
            }
        });
```
## Gradle
```
dependencies {
    compile 'com.github.kabuqinuofu:RecordingBox:V1.0.2'
}
```
