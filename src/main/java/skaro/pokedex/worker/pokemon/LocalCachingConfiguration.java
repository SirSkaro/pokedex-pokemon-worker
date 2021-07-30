package skaro.pokedex.worker.pokemon;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import skaro.pokedex.sdk.cache.NearCacheConfiguration;
import skaro.pokedex.sdk.worker.command.ratelimit.local.LocalRateLimitConfiguration;

@Configuration
@Import({
	NearCacheConfiguration.class,
	LocalRateLimitConfiguration.class
})
public class LocalCachingConfiguration {

}
