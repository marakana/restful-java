package chirp.service.representations;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import chirp.model.User;
import chirp.service.resources.PostResource;
import chirp.service.resources.UsersResource;

public class UserRepresentation {

	private final URI self;
	private final String username;
	private final String realname;
	private final URI posts;

	public UserRepresentation(User user, boolean summary) {
		String tmpUsername = user.getUsername();
		this.username = summary ? null : tmpUsername;
		this.realname = summary ? null : user.getRealname();
		this.self = UriBuilder.fromResource(UsersResource.class).path(tmpUsername)
				.build();
		this.posts = UriBuilder.fromResource(PostResource.class).build(tmpUsername);
	}

	@JsonCreator
	public UserRepresentation(@JsonProperty("self") URI self,
			@JsonProperty("username") String username,
			@JsonProperty("realname") String realname,
			@JsonProperty("posts") URI posts) {
		this.self = self;
		this.username = username;
		this.realname = realname;
		this.posts = posts;
	}

	public String getUsername() {
		return username;
	}

	public String getRealname() {
		return realname;
	}

	public URI getSelf() {
		return self;
	}

	public URI getPosts() {
		return posts;
	}

}