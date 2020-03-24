package com.example.test_sel.Callbacks;

import java.util.ArrayList;

public interface CallBack_ArrayReady<T> {

    void arrayReady(ArrayList<T> array);
    void error();

}
