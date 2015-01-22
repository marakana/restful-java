package chirp.service.resources;

import static com.google.inject.Guice.createInjector;
import static com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory.createHttpServer;

import java.io.IOException;
import java.net.URI;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.glassfish.grizzly.http.server.HttpServer;

import chirp.model.UserRepository;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.core.ClasspathResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainer;
import com.sun.jersey.test.framework.spi.container.TestContainerException;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;

/**
 * Horrible hack to augment JerseyTest with Guice. Don't look at this code,
 * you'll burn your eyes. The important thing is that getUserRepository() works.
 */
public class ResourceTest extends JerseyTest {

	private UserRepository userRepository;

	public UserRepository getUserRepository() {
		return userRepository;
	}

	@Override
	protected TestContainerFactory getTestContainerFactory() {
		return new TestContainerFactory() {

			@Override
			@SuppressWarnings("unchecked")
			public Class<LowLevelAppDescriptor> supports() {
				return LowLevelAppDescriptor.class;
			}

			@Override
			public TestContainer create(final URI baseUri,
					final AppDescriptor ad) throws IllegalArgumentException {
				return new TestContainer() {

					private HttpServer server;

					@Override
					public void stop() {
						if (server != null)
							server.stop();
						server = null;
						userRepository = null;
					}

					@Override
					public void start() {
						try {
							ResourceConfig rc = new ClasspathResourceConfig();
							userRepository = new UserRepository();
							Injector injector = createInjector(new AbstractModule() {
								@Override
								protected void configure() {
									bind(UserRepository.class).toInstance(userRepository);
								}
							});

							server = createHttpServer(baseUri, rc, new GuiceComponentProviderFactory(rc, injector));
						} catch (IOException e) {
							throw new TestContainerException(e);
						}
					}

					@Override
					public Client getClient() {
						return null;
					}

					@Override
					public URI getBaseUri() {
						return baseUri;
					}

				};
			}
		};
	}

	@Override
	protected AppDescriptor configure() {
		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(JacksonJsonProvider.class);
		return new LowLevelAppDescriptor.Builder(new ClasspathResourceConfig()).clientConfig(cc).build();
	}

}
