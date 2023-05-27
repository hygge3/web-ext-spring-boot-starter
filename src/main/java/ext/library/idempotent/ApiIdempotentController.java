package ext.library.idempotent;

import ext.library.redis.client.Redis;
import ext.library.redis.constant.RedisConstant;
import ext.library.util.IdUtils;
import ext.library.web.view.R;
import ext.library.web.view.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口幂等性
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/open/apiIdempotent")
@EnableConfigurationProperties({ApiIdempotentProperties.class})
@ConditionalOnProperty(prefix = ApiIdempotentProperties.PREFIX, name = "enabled", havingValue = "true")
public class ApiIdempotentController {

    final Redis redis;
    final ApiIdempotentProperties apiIdempotentProperties;

    /**
     * 获得幂等版本号
     */
    @GetMapping("/getVersion")
    public Result<?> getVersion() {
        String version = IdUtils.getSimpleUUID();
        String key = RedisConstant.API_IDEMPOTENT_KEY_PREFIX + version;
        redis.set(key, version, apiIdempotentProperties.getVersionTimeout());
        return R.success(version);
    }

}