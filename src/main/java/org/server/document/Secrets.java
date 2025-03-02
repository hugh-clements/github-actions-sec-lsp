package org.server.document;

import lombok.Builder;

import java.util.Map;

public class Secrets {

    @Builder
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

    public enum PermissionType{
        ACTIONS,
        ATTESTATIONS,
        CHECKS,
        CONTENTS,
        DEPLOYMENTS,
        ID_TOKENS,
        ISSUES,
        DISCUSSIONS,
        PACKAGES,
        PAGES,
        PULL_REQUESTS,
        REPOSITORY_PROJECTS,
        SECURITY_EVENTS,
        STATUSES;

        public static PermissionType stringToPermissionType(String s) {
            return switch (s) {
                case "actions" -> PermissionType.ACTIONS;
                case "attestations" -> PermissionType.ATTESTATIONS;
                case "checks" -> PermissionType.CHECKS;
                case "contents" -> PermissionType.CONTENTS;
                case "deployments" -> PermissionType.DEPLOYMENTS;
                case "id_tokens" -> PermissionType.ID_TOKENS;
                case "issues" -> PermissionType.ISSUES;
                case "discovery" -> PermissionType.DISCUSSIONS;
                case "packages" -> PermissionType.PACKAGES;
                case "pages" -> PermissionType.PAGES;
                case "pull_requests" -> PermissionType.PULL_REQUESTS;
                case "repository_projects" -> PermissionType.REPOSITORY_PROJECTS;
                case "security_events" -> PermissionType.SECURITY_EVENTS;
                default -> null;
            };
        }
    }

}
