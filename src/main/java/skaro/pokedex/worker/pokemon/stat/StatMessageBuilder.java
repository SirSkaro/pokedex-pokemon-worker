package skaro.pokedex.worker.pokemon.stat;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.discordjson.json.MessageCreateRequest;
import discord4j.discordjson.possible.Possible;
import skaro.pokeapi.resource.Name;
import skaro.pokeapi.resource.PokeApiLocaleUtils;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemon.PokemonSprites;
import skaro.pokeapi.resource.pokemon.PokemonStat;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;
import skaro.pokeapi.resource.stat.Stat;
import skaro.pokedex.sdk.client.Language;
import skaro.pokedex.sdk.discord.MessageBuilder;

public class StatMessageBuilder implements MessageBuilder<StatMessageContent> {
	private static final String PADDING = " ";
	private static final int PADDING_OFFSET = 20;
	private static final String MONOSPACE_FONT_MODIFIER_START = "`";
	private static final String MONOSPACE_FONT_MODIFIER_END = "`";
	private static final String UNDERLINE_FONT_MODIFIER_START = "__";
	private static final String UNDERLINE_FONT_MODIFIER_END = "__";
	
	public StatMessageBuilder() {
	
	}
	
	@Override
	public MessageCreateRequest populateFrom(StatMessageContent messageContent) {
		Language language = messageContent.getLanguage();
		
		return MessageCreateRequest.builder()
				.embed(EmbedData.builder()
						.title(getPokemonName(messageContent.getSpecies(), language))
						.description(formatPokemonStats(messageContent))
						.thumbnail(EmbedThumbnailData.builder()
								.url(getPokemonSprite(messageContent.getPokemon()))
								.build())
						.build())
				.build();
	}

	private Possible<String> getPokemonName(PokemonSpecies species, Language language) {
		return PokeApiLocaleUtils.getInLocale(species, language.getAbbreviation())
			.map(Name::getName)
			.map(Possible::of)
			.orElse(Possible.absent());
	}
	
	private String formatPokemonStats(StatMessageContent messageContent) {
		StringBuilder builder = new StringBuilder();
		builder.append(createRow("hp", "attack", messageContent));
		builder.append(createRow("defense", "special-attack", messageContent));
		builder.append(createRow("special-defense", "speed", messageContent));
		
		return builder.toString();
	}
	
	private Possible<String> getPokemonSprite(Pokemon pokemon) {
		return Optional.ofNullable(pokemon.getSprites())
				.map(PokemonSprites::getFrontDefault)
				.map(Possible::of)
				.orElse(Possible.absent());
	}
	
	private StringBuilder createRow(String stat1, String stat2, StatMessageContent messageContent) {
		Language language = messageContent.getLanguage();
		Pokemon pokemon = messageContent.getPokemon();
		Map<String, Stat> stats = messageContent.getStats();
		
		StringBuilder builder = new StringBuilder();
		builder.append(createRowHeader(stat1, stat2, stats, language));
		builder.append(System.lineSeparator());
		builder.append(createRowContent(stat1, stat2, pokemon));
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		return builder;
	}
	
	private StringBuilder createRowHeader(String stat1, String stat2, Map<String, Stat> stats, Language language) {
		StringBuilder builder = new StringBuilder();
		builder.append(UNDERLINE_FONT_MODIFIER_START);
		builder.append(MONOSPACE_FONT_MODIFIER_START);
		builder.append(StringUtils.rightPad(getStatInLanguage(stats.get(stat1), language), PADDING_OFFSET, PADDING));
		builder.append(getStatInLanguage(stats.get(stat2), language));
		builder.append(MONOSPACE_FONT_MODIFIER_END);
		builder.append(UNDERLINE_FONT_MODIFIER_END);
		
		return builder;
	}
	
	private StringBuilder createRowContent(String stat1, String stat2, Pokemon pokemon) {
		StringBuilder builder = new StringBuilder();
		builder.append(MONOSPACE_FONT_MODIFIER_START);
		builder.append(StringUtils.rightPad(getPokemonStat(stat1, pokemon), PADDING_OFFSET, PADDING));
		builder.append(getPokemonStat(stat2, pokemon));
		builder.append(MONOSPACE_FONT_MODIFIER_END);
		
		return builder;
	}
	
	private String getStatInLanguage(Stat stat, Language language) {
		return PokeApiLocaleUtils.getInLocale(stat, language.getAbbreviation())
				.or(() -> PokeApiLocaleUtils.getInLocale(stat, Language.ENGLISH.getAbbreviation()))
				.map(Name::getName)
				.orElse(StringUtils.EMPTY);
	}
	
	private String getPokemonStat(String statName, Pokemon pokemon) {
		return pokemon.getStats().stream()
				.filter(stat -> StringUtils.equals(statName, stat.getStat().getName()))
				.map(PokemonStat::getBaseStat)
				.map(stat -> Integer.toString(stat))
				.findFirst()
				.orElse("0");
	}
	
}
