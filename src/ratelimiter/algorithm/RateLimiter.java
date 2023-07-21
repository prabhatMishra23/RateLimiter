package ratelimiter.algorithm;

public interface RateLimiter {

    public boolean is_allowed(String key);
}
