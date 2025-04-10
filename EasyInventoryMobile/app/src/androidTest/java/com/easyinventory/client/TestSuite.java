package com.easyinventory.client;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.stream.Collectors;

import easyinventoryapi.API;
import easyinventoryapi.APIException;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoginTest.class,
        UsersListTest.class,
        UsersTest.class,
        ItemsListTest.class,
        ItemsTest.class,
        WarehouseListTest.class,
        WarehouseTest.class,
        UnitListTest.class,
        UnitTest.class,
        StorageLimitTest.class,
        StorageListTest.class,
        StorageTest.class,
        SearchTest.class,
        OperationTest.class,
        PasswordChangeTest.class,
        AuthorizationTest.class,
        ScreenshotTest.class
})
public class TestSuite {
    public static final int RANDOM=new Random().nextInt();

    public static void screenshot(String name) {
        try {
            var f = new File("/storage/emulated/0/Pictures/" + name);

            var wr = new FileOutputStream(f);
            var bm= InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
            System.out.println(bm.getWidth()+" x "+bm.getHeight());
            bm.compress(Bitmap.CompressFormat.PNG, 70, wr);
            wr.flush();
            wr.close();
            System.out.println("written "+f.getAbsolutePath());
            System.out.println(f.length());
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    public static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Matcher<View> withData(final Matcher<Object> dataMatcher) {
        return new TypeSafeMatcher<>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("with class name: ");
                dataMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof AdapterView)) {
                    return false;
                }

                @SuppressWarnings("rawtypes")
                Adapter adapter = ((AdapterView) view).getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (dataMatcher.matches(adapter.getItem(i))) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    @BeforeClass
    public static void loadTestData() {
        try {
            InputStream raw = InstrumentationRegistry.getInstrumentation().getContext().getResources().openRawResource(com.easyinventory.client.test.R.raw.testdata);
            BufferedReader r = new BufferedReader(new InputStreamReader(raw, StandardCharsets.UTF_8));
            String data=r.lines().collect(Collectors.joining());
            r.close();

            API.putDB(new AndroidHTTPConnector(false), "http://10.0.2.2:84/api", data, "tester");
        } catch (IOException | APIException e) {
            throw new RuntimeException(e);
        }
    }
}
