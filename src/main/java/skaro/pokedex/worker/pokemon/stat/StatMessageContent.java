package skaro.pokedex.worker.pokemon.stat;

import java.util.Map;

import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;
import skaro.pokeapi.resource.stat.Stat;
import skaro.pokedex.sdk.client.Language;
import skaro.pokedex.sdk.discord.MessageContent;

public class StatMessageContent implements MessageContent {

	private Language language;
	private Pokemon pokemon;
	private PokemonSpecies species;
	private Map<String, Stat> stats;
	
	@Override
	public Language getLanguage() {
		return language;
	}
	public Pokemon getPokemon() {
		return pokemon;
	}
	public void setPokemon(Pokemon pokemon) {
		this.pokemon = pokemon;
	}
	public PokemonSpecies getSpecies() {
		return species;
	}
	public void setSpecies(PokemonSpecies species) {
		this.species = species;
	}
	public Map<String, Stat> getStats() {
		return stats;
	}
	public void setStats(Map<String, Stat> stats) {
		this.stats = stats;
	}
	public void setLanguage(Language language) {
		this.language = language;
	}

}
