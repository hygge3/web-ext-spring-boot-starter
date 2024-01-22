package ext.library.idempotent;

import ext.library.redis.constant.RedisConstant;
import ext.library.redis.utils.RedisUtils;
import ext.library.util.IdUtils;
import ext.library.web.view.R;
import ext.library.web.view.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * 接口幂等性
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/open/apiIdempotent")
@ConditionalOnProperty(prefix = ApiIdempotentProperties.PREFIX, name = "enabled", havingValue = "true")
public class ApiIdempotentController {

    final ApiIdempotentProperties apiIdempotentProperties;

    /**
     * 获得幂等版本号
     */
    @GetMapping("/getVersion")
    public Result<?> getVersion() {
        String version = IdUtils.getSimpleUUID();
        String key = RedisConstant.API_IDEMPOTENT_KEY_PREFIX + version;
        RedisUtils.set(key, version, Duration.ofSeconds(apiIdempotentProperties.getVersionTimeout()));
        return R.success(version);
    }

}
