package com.example.test_sel.Callbacks;

import java.util.HashMap;

public interface CallBack_HashMapReady<T> {

    void hashMapReady(HashMap<String, T> hashMap);
    void error();


}
