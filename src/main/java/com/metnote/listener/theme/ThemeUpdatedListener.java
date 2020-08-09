package com.metnote.listener.theme;

import com.metnote.cache.AbstractStringCacheStore;
import com.metnote.event.options.OptionUpdatedEvent;
import com.metnote.event.theme.ThemeUpdatedEvent;
import com.metnote.service.ThemeService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Theme updated listener.
 *
 * @author johnniang
 * @date 19-4-29
 */
@Component
public class ThemeUpdatedListener {

    private final AbstractStringCacheStore cacheStore;

    public ThemeUpdatedListener(AbstractStringCacheStore cacheStore) {
        this.cacheStore = cacheStore;
    }

    @EventListener
    public void onApplicationEvent(ThemeUpdatedEvent event) {
        cacheStore.delete(ThemeService.THEMES_CACHE_KEY);
    }

    @EventListener
    public void onOptionUpdatedEvent(OptionUpdatedEvent optionUpdatedEvent) {
        cacheStore.delete(ThemeService.THEMES_CACHE_KEY);
    }
}
