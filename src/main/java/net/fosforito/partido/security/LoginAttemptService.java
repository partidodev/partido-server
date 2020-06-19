package net.fosforito.partido.security;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * This class tries to avoid brute force attacks by blocking login
 * attempts for IPs that already failed to login a few times.
 * Failed IPs are cached for a defined period of time and cleared automatically.
 *
 * This is part 1 of the Partido Anti Brute Forcing Concept.
 * Part 2 is blocking IPs longer with fail2ban externally when an IP was blocked here more than once.
 */
@Service
public class LoginAttemptService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoginAttemptService.class);

  private final int MAX_ATTEMPT = 3;
  private LoadingCache<String, Integer> attemptsCache;

  public LoginAttemptService() {
    super();
    attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
      @Override
      public Integer load(final String key) {
        return 0;
      }
    });
  }

  public void loginSucceeded(final String key) {
    attemptsCache.invalidate(key);
  }

  public void loginFailed(final String key) {
    int attempts = 0;
    try {
      attempts = attemptsCache.get(key);
    } catch (final ExecutionException e) {
      attempts = 0;
    }
    attempts++;
    LOGGER.warn("Login attempt for ip {} failed", key);
    attemptsCache.put(key, attempts);
  }

  public boolean isBlocked(final String key) {
    try {
      return attemptsCache.get(key) >= MAX_ATTEMPT;
    } catch (final ExecutionException e) {
      return false;
    }
  }
}