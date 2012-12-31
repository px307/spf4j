 /*
 * Copyright (c) 2001, Zoltan Farkas All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.zoltran.perf;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author zoly
 */
@SuppressWarnings("SleepWhileInLoop")
public class RecorderFactoryTest {
    

    private long startTime = System.currentTimeMillis();
    
        @BeforeClass
    public static void init() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                StringWriter strw = new StringWriter();
                e.printStackTrace(new PrintWriter(strw));
                Assert.fail("Got Exception: " + strw.toString());
            }
        });
    }
    
    
    /**
     * Test of createScalableQuantizedRecorder method, of class RecorderFactory.
     */
    @Test
    public void testCreateScalableQuantizedRecorder() throws IOException, InterruptedException {
        System.out.println("createScalableQuantizedRecorder");
        Object forWhat = "test1";
        String unitOfMeasurement = "ms";
        int sampleTime = 1000;
        int factor = 10;
        int lowerMagnitude = 0;
        int higherMagnitude = 3;
        int quantasPerMagnitude = 10;
        MeasurementRecorder result = RecorderFactory.createScalableQuantizedRecorder(
                forWhat, unitOfMeasurement, sampleTime, factor, lowerMagnitude, higherMagnitude, quantasPerMagnitude);
        for (int i=0; i<500; i++) {
            result.record(i);
            Thread.sleep(20);
        }
        long endTime = System.currentTimeMillis();
        System.out.println(RecorderFactory.RRD_DATABASE.generateCharts(startTime, endTime, 1200, 600));
        System.out.println(RecorderFactory.RRD_DATABASE.getMeasurements());
        ((Closeable)result).close();
        
    }

    /**
     * Test of createScalableQuantizedRecorderSource method, of class RecorderFactory.
     */
    @Test
    public void testCreateScalableQuantizedRecorderSource() throws IOException, InterruptedException {
        System.out.println("createScalableQuantizedRecorderSource");
        Object forWhat = "bla";
        String unitOfMeasurement = "ms";
        int sampleTime = 1000;
        int factor = 10;
        int lowerMagnitude = 0;
        int higherMagnitude = 3;
        int quantasPerMagnitude = 10;
        MeasurementRecorderSource result = RecorderFactory.createScalableQuantizedRecorderSource(
                forWhat, unitOfMeasurement, sampleTime, factor, lowerMagnitude, higherMagnitude, quantasPerMagnitude);
        for (int i=0; i<5000; i++) {
            result.getRecorder("X"+ i%2).record(i);
            Thread.sleep(1);
        }
        long endTime = System.currentTimeMillis();
        System.out.println(RecorderFactory.RRD_DATABASE.generateCharts(startTime, endTime, 1200, 600));
        System.out.println(RecorderFactory.RRD_DATABASE.getMeasurements());
        ((Closeable)result).close();
    }
    
    
    
    
    @Test
    public void testOutofQuantizedZoneValues() throws IOException, InterruptedException {
        System.out.println("testOutofQuantizedZoneValues");
        Object forWhat = "largeVals";
        String unitOfMeasurement = "ms";
        int sampleTime = 1000;
        int factor = 10;
        int lowerMagnitude = 0;
        int higherMagnitude = 3;
        int quantasPerMagnitude = 10;
        MeasurementRecorder result = RecorderFactory.createScalableQuantizedRecorder(
                forWhat, unitOfMeasurement, sampleTime, factor, lowerMagnitude, higherMagnitude, quantasPerMagnitude);
        for (int i=0; i<500; i++) {
            result.record(10000);
            Thread.sleep(20);
        }
        long endTime = System.currentTimeMillis();
        System.out.println(RecorderFactory.RRD_DATABASE.generateCharts(startTime, endTime, 1200, 1600));
        System.out.println(RecorderFactory.RRD_DATABASE.getMeasurements());
        ((Closeable)result).close();
        
    }
    
    
    public static void main(String[] args) throws IOException, InterruptedException {
       new RecorderFactoryTest(). testCreateScalableQuantizedRecorder();
    }
    
}
