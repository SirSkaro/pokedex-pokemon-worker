package skaro.pokedex.worker.pokemon.stat;

import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

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
	private static final String BAR_BORDER_START_CHARACTER = "╠";
	private static final String BAR_UNIT_CHARACTER = "█";
	private static final String BAR_BORDER_END_CHARACTER = "╣";
	private static final int MAX_BAR_LENGTH = 24;
	private static final String MONOSPACE_FONT_MODIFIER_START = "`";
	private static final String MONOSPACE_FONT_MODIFIER_END = "`";
	private static final String UNDERLINE_FONT_MODIFIER_START = "__";
	private static final String UNDERLINE_FONT_MODIFIER_END = "__";
	private static final double MAX_BASE_STATE = 255.0;
	
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
		builder.append(createRow("hp", messageContent));
		builder.append(createRow("attack", messageContent));
		builder.append(createRow("defense", messageContent));
		builder.append(createRow("special-attack", messageContent));
		builder.append(createRow("special-defense", messageContent));
		builder.append(createRow("speed", messageContent));
		
		return builder.toString();
	}
	
	private Possible<String> getPokemonSprite(Pokemon pokemon) {
		return Optional.ofNullable(pokemon.getSprites())
				.map(PokemonSprites::getFrontDefault)
				.map(Possible::of)
				.orElse(Possible.absent());
	}
	
	private StringBuilder createRow(String stat, StatMessageContent messageContent) {
		Pokemon pokemon = messageContent.getPokemon();
		
		StringBuilder builder = new StringBuilder();
		builder.append(createRowHeader(stat, messageContent));
		builder.append(System.lineSeparator());
		builder.append(createRowContent(stat, pokemon));
		builder.append(System.lineSeparator());
		return builder;
	}
	
	private StringBuilder createRowHeader(String statName, StatMessageContent messageContent) {
		Language language = messageContent.getLanguage();
		int baseStatValue = getPokemonStat(statName, messageContent.getPokemon());
		Map<String, Stat> stats = messageContent.getStats();
		
		StringBuilder builder = new StringBuilder();
		builder.append(UNDERLINE_FONT_MODIFIER_START);
		builder.append(getStatInLanguage(stats.get(statName), language));
		builder.append(String.format(" - %d", baseStatValue));
		builder.append(UNDERLINE_FONT_MODIFIER_END);
		
		return builder;
	}
	
	private StringBuilder createRowContent(String statName, Pokemon pokemon) {
		int baseStatValue = getPokemonStat(statName, pokemon);
		
		StringBuilder builder = new StringBuilder();
		builder.append(MONOSPACE_FONT_MODIFIER_START);
		builder.append(BAR_BORDER_START_CHARACTER);
		builder.append(StringUtils.rightPad(createStatBar(baseStatValue), MAX_BAR_LENGTH, PADDING));
		builder.append(BAR_BORDER_END_CHARACTER);
		builder.append(MONOSPACE_FONT_MODIFIER_END);
		
		return builder;
	}
	
	private String getStatInLanguage(Stat stat, Language language) {
		return PokeApiLocaleUtils.getInLocale(stat, language.getAbbreviation())
				.or(() -> PokeApiLocaleUtils.getInLocale(stat, Language.ENGLISH.getAbbreviation()))
				.map(Name::getName)
				.orElse(StringUtils.EMPTY);
	}
	
	private int getPokemonStat(String statName, Pokemon pokemon) {
		return pokemon.getStats().stream()
				.filter(stat -> StringUtils.equals(statName, stat.getStat().getName()))
				.map(PokemonStat::getBaseStat)
				.findFirst()
				.orElse(0);
	}
	
	private String createStatBar(int baseStat) {
		StringBuilder builder = new StringBuilder();
		int barLength = (int)(MAX_BAR_LENGTH * ((double)baseStat / MAX_BASE_STATE));
		IntStream.range(0, barLength)
			.forEach(barUnit -> builder.append(BAR_UNIT_CHARACTER));
			
		return builder.toString();
	}
	
}
