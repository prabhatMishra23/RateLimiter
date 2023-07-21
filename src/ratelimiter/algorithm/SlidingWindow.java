package ratelimiter.algorithm;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * we maintain request_count in current window size.
 * Each request will be represented by a timestamp (one to one mapping)
 * This handles concurrent requests scenarios as well
 * we also maintain a last_start_time this is used to see if we need to update our linkedHasMap
 * if current_time_stamp-window_size > last_start_time we update our linked hashMap to remove all older times
 */
public class SlidingWindow implements RateLimiter{

    class Window{
        LinkedHashMap<Long, Integer> timestamp_count = new LinkedHashMap<>();
        int request_count;

        int window_length;

        public Window(int window_length){
            this.window_length = window_length;
        }

        public int getRequestCount(){
            return this.request_count;
        }

        public int getWindow_length(){
            return this.window_length;
        }

        public void addTimeStamp(long currentTimeStamp){
            timestamp_count.put(currentTimeStamp, timestamp_count.getOrDefault(currentTimeStamp,0)+1);
            request_count++;
        }

        public void updateWindow(long currentTimeStamp){
            Iterator<Long> iterator = timestamp_count.keySet().iterator();
            while (iterator.hasNext()) {
                long key = iterator.next();
                if (key < currentTimeStamp - window_length) {
                    request_count -= timestamp_count.get(key);
                    iterator.remove();
                }
            }
        }
    }

    long last_start_time;
    long current_time_stamp;

    int request_count_limit;


    Map<String, Window> keyValue = new HashMap<>();

    public SlidingWindow(String key){
        this.current_time_stamp = getCurrentTimeStamp();
        this.last_start_time = current_time_stamp;
        if(!keyValue.containsKey(key)){
            Window window = new Window(5);
            keyValue.put(key, window);
        }
        this.request_count_limit = 10; // can use key to read from config.
    }

    private long getCurrentTimeStamp() {
        return Instant.now().toEpochMilli();
    }

    /**
     * when is allowed is called that means request has arrived into the system
     * i need to see if I need to reject or accept that request
     *  if request count is greater than request limit reject request and such request should not get registered however
     *  the timestamp could change the count of window and update request count which could change
     * @param key
     * @return
     */
    @Override
    public boolean is_allowed(String key) {
        Window window = keyValue.get(key);
        long current_time_stamp = getCurrentTimeStamp();
        if( current_time_stamp - window.getWindow_length() > last_start_time){
            window.updateWindow(current_time_stamp);
            last_start_time = current_time_stamp;
        }
        if(window.getRequestCount() > request_count_limit) {
            return false;
        }
        window.addTimeStamp(current_time_stamp);
        return true;
    }
}
