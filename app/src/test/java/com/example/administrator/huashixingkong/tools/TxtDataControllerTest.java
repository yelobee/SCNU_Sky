package com.example.administrator.huashixingkong.tools;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by yelobee_ on 16/4/21.
 */
public class TxtDataControllerTest {
    private String path="/scnu/map_positions_config.txt";
    private String data="data";
    private TxtDataController mTxtDataController;

    @Before
    public void setUp() throws Exception {
        mTxtDataController=new TxtDataController(path);
    }

    @Test(expected = FileNotFoundException.class)
    public void testGetData() throws Exception {
        when(mTxtDataController.getData()).thenReturn("get it");
        assertEquals("get it", mTxtDataController.getData(), 0);
    }

    @Test(expected = IOException.class)
    public void testWriteData() throws Exception {
       mTxtDataController.writeData(data);
    }
}