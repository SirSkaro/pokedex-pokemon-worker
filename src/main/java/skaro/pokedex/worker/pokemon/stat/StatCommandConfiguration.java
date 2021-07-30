package skaro.pokedex.worker.pokemon.stat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import skaro.pokedex.sdk.discord.DiscordMessageDirector;
import skaro.pokedex.sdk.discord.DiscordRouterFacade;
import skaro.pokedex.sdk.discord.MessageCreateRequestDirector;

@Configuration
public class StatCommandConfiguration {
	private static final String STAT_MESSAGE_DIRECTOR_BEAN = "statMessageDirector";
	
	@Bean(STAT_MESSAGE_DIRECTOR_BEAN)
	public DiscordMessageDirector<StatMessageContent> expectedArgumentsDirector(DiscordRouterFacade router) {
		StatMessageBuilder messageBuilder = new StatMessageBuilder();
		return new MessageCreateRequestDirector<StatMessageContent>(router, messageBuilder);
	}
	
	
	
}
