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
package org.spf4j.perf.impl;

import org.spf4j.base.AbstractRunnable;
import org.spf4j.base.DefaultScheduler;
import org.spf4j.perf.EntityMeasurements;
import org.spf4j.perf.EntityMeasurementsInfo;
import org.spf4j.perf.MeasurementDatabase;
import org.spf4j.perf.MeasurementProcessor;
import org.spf4j.perf.MeasurementRecorder;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import javax.annotation.concurrent.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zoly
 */
@ThreadSafe
public class ScalableMeasurementRecorder implements MeasurementRecorder, EntityMeasurements, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(ScalableMeasurementRecorder.class);
    
    private final Map<Thread, MeasurementProcessor> threadLocalRecorders;
    private final ThreadLocal<MeasurementProcessor> threadLocalRecorder;
    private final ScheduledFuture<?> samplingFuture;
    private final MeasurementProcessor processorTemplate;

    public ScalableMeasurementRecorder(final MeasurementProcessor processor, final int sampleTimeMillis, final MeasurementDatabase database) {
        threadLocalRecorders = new HashMap<Thread, MeasurementProcessor>();
        processorTemplate = processor;
        threadLocalRecorder = new ThreadLocal<MeasurementProcessor>() {

            @Override
            protected MeasurementProcessor initialValue() {
                MeasurementProcessor result = (MeasurementProcessor) processor.createClone(false);
                synchronized (threadLocalRecorders) {
                    threadLocalRecorders.put(Thread.currentThread(), result);
                }
                return result;
            }
        };
        try {
            database.alocateMeasurements(processor.getInfo(), sampleTimeMillis);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        samplingFuture = DefaultScheduler.scheduleAllignedAtFixedRateMillis(new AbstractRunnable(true) {
            private volatile long lastRun = 0;
            
            @Override
            public void doRun() throws IOException {
                long currentTime = System.currentTimeMillis();
                if (currentTime > lastRun) {
                    lastRun = currentTime;
                    database.saveMeasurements(ScalableMeasurementRecorder.this.getInfo(), 
                            ScalableMeasurementRecorder.this.getMeasurements(true),
                            currentTime, sampleTimeMillis);
                } else {
                    LOG.warn("Last measurement recording was at {} current run is {}, something is wrong" , lastRun, currentTime);
                }
            }
        }, sampleTimeMillis);

        
    }

    @Override
    public void record(long measurement) {
        threadLocalRecorder.get().record(measurement);
    }

    @Override
    public long [] getMeasurements(boolean reset) {
        EntityMeasurements result  = null;
        synchronized (threadLocalRecorders) {
            List<Thread> removeThreads = new ArrayList<Thread>();
            for (Map.Entry<Thread, MeasurementProcessor> entry: threadLocalRecorders.entrySet()) {
                if (!entry.getKey().isAlive() && reset) {
                    removeThreads.add(entry.getKey());
                }
                EntityMeasurements measurements = entry.getValue().createClone(reset);  
                if (result == null) {
                    result = measurements;
                }
                else {
                    result = result.aggregate(measurements);
                }
            }
            for (Thread t : removeThreads) {
                threadLocalRecorders.remove(t);
            }
        }
        return (result == null) ? processorTemplate.getMeasurements(false) : result.getMeasurements(false);
    }

    @Override
    public EntityMeasurements aggregate(EntityMeasurements mSource) {
        throw new UnsupportedOperationException("Aggregating Scalable Recorders not supported");
    }



    @Override
    public EntityMeasurements createClone(boolean reset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
        samplingFuture.cancel(false);             
    }

    @Override
    protected void finalize() throws Throwable {
        try{
            super.finalize();
        } finally {
            this.close();
        }
    }

    @Override
    public String toString() {
        return "ScalableMeasurementRecorder{" + "threadLocalRecorders=" + threadLocalRecorders + ", processorTemplate=" + processorTemplate + '}';
    }

    @Override
    public EntityMeasurements createLike(Object entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityMeasurementsInfo getInfo() {
        return processorTemplate.getInfo();
    }

    
    
    
}