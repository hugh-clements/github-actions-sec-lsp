package org.server.document;

import java.util.List;

public class Secrets {

    public record Secret(
            String accessToken,
            String description,
            Boolean required
    ) {}

    public enum PermissionLevel{
        NONE,
        READ,
        WRITE
    }

    public record Permissions(
            PermissionLevel actions,
            PermissionLevel attestations,
            PermissionLevel checks,
            PermissionLevel contents,
            PermissionLevel deployments,
            PermissionLevel idToken,
            PermissionLevel issues,
            PermissionLevel discussions,
            PermissionLevel packages,
            PermissionLevel pages,
            PermissionLevel pullRequests,
            PermissionLevel repositoryProjects,
            PermissionLevel securityEvents,
            PermissionLevel statuses
    ) {}
}
