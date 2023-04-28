package com.alageek.chatgpt.task;

import com.alageek.chatgpt.cache.SseEmitterCache;
import com.alageek.chatgpt.constant.CommonConstant;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
public class ClearTask {

    @Scheduled(cron = "0 0 1 */1 * ?")
    private void clear() {
        List<String> uuids = new ArrayList<>();

        String today = CommonConstant.yyyyMMdd.format(new Date());
        String yesterday = CommonConstant.yyyyMMdd.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));

        SseEmitterCache.UUID_DATE.forEach((key, value) -> {
            if (!today.equals(value) && !yesterday.equals(value)) {
                uuids.add(key);
            }
        });

        for (String uuid : uuids) {
            SseEmitterCache.UUID_DATE.remove(uuid);
            SseEmitterCache.UUID_SSE_EMITTER.remove(uuid);
            SseEmitterCache.UUID_CONTENT.remove(uuid);
            SseEmitterCache.UUID_CONTENT_LENGTH.remove(uuid);
        }
    }

}
