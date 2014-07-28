package com.example.dawidr.androidtestproject;

import android.app.Application;
import android.test.ApplicationTestCase;

public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void test_first() throws Exception {
        final int variable1 = 5;
        final int variable2 = 5;
        assertEquals(variable1, variable2);
    }

//    public void test_second() throws Exception {
//        final int variable1 = 5;
//        final int variable2 = 2;
//        assertEquals(variable1, variable2);
//    }
}