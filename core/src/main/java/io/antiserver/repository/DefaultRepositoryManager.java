package io.antiserver.repository;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import io.antiserver.api.AntiserverDependency;
import io.antiserver.api.AntiserverMavenDependency;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;

public class DefaultRepositoryManager implements RepositoryManager {

    private RepositorySystem system;

    private DefaultRepositorySystemSession session;

    private List<RemoteRepository> repositories;

    private CachingTreeResolver resolver;

    public DefaultRepositoryManager(String localRepoPath, List<AntiserverMavenDependency> managedDependencies) {
        if (!new File(localRepoPath).mkdirs()) {
            throw new IllegalArgumentException("Cannot read or create repository location at " + localRepoPath);
        }
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        this.system = locator.getService(RepositorySystem.class);

        RepositoryPolicy fastestPolicy = new RepositoryPolicy(true, RepositoryPolicy.UPDATE_POLICY_NEVER, RepositoryPolicy.CHECKSUM_POLICY_IGNORE);

        RemoteRepository localRepo = new RemoteRepository.Builder("antiserver.repo", "default", new File(localRepoPath).toURI().toString())
                .setSnapshotPolicy(fastestPolicy)
                .setReleasePolicy(fastestPolicy)
                .build();

        RemoteRepository proxy = new RemoteRepository.Builder("proxy", "default", "http://localhost:8765/nexus/content/groups/public")
                .setSnapshotPolicy(fastestPolicy)
                .setReleasePolicy(fastestPolicy)
                .build();

        RemoteRepository mavenCentral = new RemoteRepository.Builder("central", "default", "http://repo1.maven.org/maven2/")
                .setSnapshotPolicy(fastestPolicy)
                .setReleasePolicy(fastestPolicy)
                .build();

        this.repositories = Arrays.asList(localRepo, proxy, mavenCentral);

        this.session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepository = new LocalRepository(localRepoPath);
        this.session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepository));

        List<Dependency> managedDeps = resolveBoms(managedDependencies);
        this.resolver = new CachingTreeResolver(this.system, this.session, this.repositories, managedDeps);
    }

    @Override
    public CompletableFuture<List<URL>> classpath(List<AntiserverMavenDependency> dependencies) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                List<DependencyNode> children = dependencies.stream()
                        .map(AntiserverMavenDependency::getGav)
                        .map(resolver::resolve)
                        .collect(Collectors.toList());

                Artifact artifact = new DefaultArtifact("org.run:run-app:pom:1.0.0");
                DefaultDependencyNode root = new DefaultDependencyNode(new Dependency(artifact, "compile"));
                root.setChildren(children);

                PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
                root.accept(nlg);

                return nlg.getArtifacts(true).stream()
                        .filter(a -> !a.toString().equals(artifact.toString()))
                        .peek(a -> {
                            if (a.getFile() == null){
                                throw new RuntimeException("Unable to resolveDependencies artifact " + a);
                            }
                        })
                        .map(this::toURL)
                        .collect(Collectors.toList());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public CompletableFuture<Void> preload(List<AntiserverMavenDependency> dependencies) {
        return CompletableFuture.supplyAsync(() -> {
            dependencies.stream()
                    .map(AntiserverMavenDependency::getGav)
                    .forEach(resolver::resolve);
            return null;
        });
    }

    private URL toURL(Artifact artifact) {
        try {
            return artifact.getFile().toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Dependency> resolveBoms(List<AntiserverMavenDependency> managedDependencies) {
        try {
            List<Dependency> managed = new ArrayList<>();
            for (AntiserverMavenDependency manDependency : managedDependencies) {
                Artifact bom = new DefaultArtifact(manDependency.getGav());
                if (!"pom".equals(bom.getExtension())) {
                    throw new IllegalArgumentException("Bom " + bom + " is not of type 'pom'");
                }
                ArtifactDescriptorRequest request = new ArtifactDescriptorRequest(bom, this.repositories, null);
                ArtifactDescriptorResult result = system.readArtifactDescriptor(session, request);
                managed.addAll(result.getManagedDependencies());
            }

            return managed;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
