package skaro.pokedex.worker.pokemon.stat;

import java.lang.invoke.MethodHandles;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;
import skaro.pokeapi.resource.stat.Stat;
import skaro.pokedex.sdk.discord.DiscordMessageDirector;
import skaro.pokedex.sdk.messaging.dispatch.AnsweredWorkRequest;
import skaro.pokedex.sdk.messaging.dispatch.WorkRequest;
import skaro.pokedex.sdk.messaging.dispatch.WorkStatus;
import skaro.pokedex.sdk.worker.command.Command;

@Component("statCommand")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StatCommand implements Command {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private PokeApiClient pokeApiClient;
	private DiscordMessageDirector<StatMessageContent> director; 

	public StatCommand(PokeApiClient pokeApiClient, DiscordMessageDirector<StatMessageContent> director) {
		this.pokeApiClient = pokeApiClient;
		this.director = director;
	}
	
	@Override
	public Mono<AnsweredWorkRequest> execute(WorkRequest request) {
		String pokemonName = request.getArguments().get(0);
		
		return gatherResources(pokemonName)
			.map(zippedResources -> createMessageContent(zippedResources, request))
			.flatMap(messageContent -> director.createDiscordMessage(messageContent, request.getChannelId()))
			.thenReturn(createAnswer(request));
	}
	
	private Mono<Tuple2<Tuple2<Pokemon, PokemonSpecies>, Map<String, Stat>>> gatherResources(String pokemonName) {
		return pokeApiClient.getResource(Pokemon.class, pokemonName)
				.zipWhen(this::getSpecies)
				.zipWith(getStats());
	}
	
	private Mono<PokemonSpecies> getSpecies(Pokemon pokemon) {
		return pokeApiClient.followResource(pokemon::getSpecies, PokemonSpecies.class);
	}
	
	private Mono<Map<String, Stat>> getStats() {
		return Flux.range(1, 6)
			.flatMap(id -> pokeApiClient.getResource(Stat.class, Integer.toString(id)))
			.collectMap(Stat::getName);
	}
	
	private StatMessageContent createMessageContent(Tuple2<Tuple2<Pokemon, PokemonSpecies>, Map<String, Stat>> zippedResources, WorkRequest request) {
		StatMessageContent messageContent = new StatMessageContent();
		messageContent.setPokemon(zippedResources.getT1().getT1());
		messageContent.setSpecies(zippedResources.getT1().getT2());
		messageContent.setStats(zippedResources.getT2());
		messageContent.setLanguage(request.getLanguage());
		
		return messageContent;
	}
	
	private AnsweredWorkRequest createAnswer(WorkRequest request) {
		AnsweredWorkRequest answer = new AnsweredWorkRequest();
		answer.setStatus(WorkStatus.SUCCESS);
		answer.setWorkRequest(request);
		
		return answer;
	}

}
