package com.yc.recordlibrary.box;

/**
 * @author by kabuqinuofu on 2018/3/15.
 */
public interface RecordListener {

    /**
     * 录音完成
     *
     * @param filtPath 文件路径
     * @param time     录制时长
     */
    void finish(String filtPath, long time);

    /**
     * 取消录音
     */
    void cancle();

}
