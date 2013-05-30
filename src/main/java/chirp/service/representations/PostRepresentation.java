package chirp.service.representations;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import chirp.model.Post;
import chirp.service.resources.PostResource;
import chirp.service.resources.UsersResource;

public class PostRepresentation {

	private final URI self;
	private final String content;
	private final String timestamp;
	private final URI user;

	public PostRepresentation(Post post) {
		String username = post.getUser().getUsername();
		this.content = post.getContent();
		this.timestamp = post.getTimestamp().toString();
		this.self = UriBuilder.fromResource(PostResource.class)
				.path(this.timestamp).build(username);
		this.user = UriBuilder.fromResource(UsersResource.class).path(username)
				.build();
	}

	@JsonCreator
	public PostRepresentation(@JsonProperty("self") URI self,
			@JsonProperty("content") String content,
			@JsonProperty("timestamp") String timestamp,
			@JsonProperty("user") URI user) {
		this.content = content;
		this.timestamp = timestamp;
		this.self = self;
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public URI getSelf() {
		return self;
	}

	public URI getUser() {
		return user;
	}

}
