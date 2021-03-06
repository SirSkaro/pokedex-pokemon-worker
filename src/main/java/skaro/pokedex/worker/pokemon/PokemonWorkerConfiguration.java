package skaro.pokedex.worker.pokemon;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import skaro.pokeapi.PokeApiReactorCachingConfiguration;
import skaro.pokedex.sdk.worker.WorkerDiscordConfiguration;
import skaro.pokedex.sdk.worker.WorkerMessageListenConfiguration;
import skaro.pokedex.sdk.worker.WorkerResourceConfiguration;
import skaro.pokedex.sdk.worker.command.DefaultWorkerCommandConfiguration;

@Configuration
@Import({
	WorkerResourceConfiguration.class,
	WorkerDiscordConfiguration.class, 
	WorkerMessageListenConfiguration.class,
	DefaultWorkerCommandConfiguration.class,
	PokeApiReactorCachingConfiguration.class
})
public class PokemonWorkerConfiguration {

	
}
