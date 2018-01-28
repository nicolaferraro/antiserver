package io.antiserver.repository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;

public class CachingTreeResolver {

    private RepositorySystem system;

    private DefaultRepositorySystemSession session;

    private List<RemoteRepository> repositories;

    private ConcurrentHashMap<String, DependencyNode> cache;

    List<Dependency> managedDependencies;

    public CachingTreeResolver(RepositorySystem system, DefaultRepositorySystemSession session, List<RemoteRepository> repositories, List<Dependency> managedDependencies) {
        this.system = system;
        this.session = session;
        this.repositories = repositories;
        this.managedDependencies = managedDependencies;
        this.cache = new ConcurrentHashMap<>();
    }

    public DependencyNode resolve(String gav) {
        return cache.computeIfAbsent(gav, this::doResolve);
    }

    private DependencyNode doResolve(String gav) {
        try {
            Dependency dependency = new Dependency(new DefaultArtifact(gav), "compile");

            CollectRequest collectRequest = new CollectRequest();
            collectRequest.setRepositories(repositories);
            collectRequest.setRoot(dependency);
            collectRequest.setManagedDependencies(this.managedDependencies);

            DependencyNode node = system.collectDependencies(session, collectRequest).getRoot();

            DependencyRequest request = new DependencyRequest();
            request.setRoot(node);

            DependencyResult result = system.resolveDependencies(session, request);
            if (result.getCollectExceptions() != null && result.getCollectExceptions().size() > 0) {
                throw new RuntimeException("Found " + result.getCollectExceptions().size() + " exceptions while collecting dependencies. First one is", result.getCollectExceptions().get(0));
            }

            return result.getRoot();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
