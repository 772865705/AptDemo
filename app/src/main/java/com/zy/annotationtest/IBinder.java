package com.zy.annotationtest;

import android.app.Activity;

/**
 * Created by ZhaoYue on 2022/1/2.
 */
public interface IBinder<T extends Activity> {
    void bind(T target);
}
