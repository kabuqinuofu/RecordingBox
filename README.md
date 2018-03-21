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
## License	
``` xml	
Copyright 2018 	kabuqinuofu
Licensed under the Apache License, Version 2.0 (the "License");	
you may not use this file except in compliance with the License.	
You may obtain a copy of the License at	
	
   http://www.apache.org/licenses/LICENSE-2.0	
	
Unless required by applicable law or agreed to in writing, software	
distributed under the License is distributed on an "AS IS" BASIS,	
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.	
See the License for the specific language governing permissions and	
limitations under the License.	
```
