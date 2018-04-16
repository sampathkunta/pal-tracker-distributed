package io.pivotal.pal.tracker.backlog;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String endpoint;

    private final ConcurrentMap<Long, ProjectInfo> projects;

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
        this.projects = new ConcurrentHashMap<>();
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo pi = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        projects.put(projectId, pi);
        return pi;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        return projects.get(projectId);
    }
}
