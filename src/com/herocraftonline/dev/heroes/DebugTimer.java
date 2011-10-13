package com.herocraftonline.dev.heroes;

import java.util.HashMap;
import java.util.Map;

public class DebugTimer {

    private long origin = System.nanoTime();
    private Map<String, Long> starts = new HashMap<String, Long>();
    private Map<String, Long> lengths = new HashMap<String, Long>();
    
    public void reset() {
        origin = System.nanoTime();
        starts.clear();
        lengths.clear();
    }
    
    public void startTask(String task) {
        starts.put(task, System.nanoTime());
    }
    
    public void stopTask(String task) {
        if (starts.containsKey(task)) {
            if (lengths.containsKey(task))
                lengths.put(task, System.nanoTime() - starts.get(task) + lengths.get(task));
            else
                lengths.put(task, System.nanoTime() - starts.get(task));
        }
    }
    
    public double getRelativeTimeSpent(String task) {
        if (lengths.containsKey(task)) {
            double total = System.nanoTime() - origin;
            return lengths.get(task) / total;
        }
        
        return 0;
    }
    
    public String dump() {
        double total = ((double) System.nanoTime() - origin) / 1000000000.0;
        String output = String.format("Total run time: %.2fs\n", total);
        for (Map.Entry<String, Long> entry : lengths.entrySet()) {
            output += String.format("%s:\t\t%.4f%%\n", entry.getKey(), getRelativeTimeSpent(entry.getKey()) * 100);
        }
        return output;
    }
    
}
