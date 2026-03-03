package br.com.novalearn.platform.domain.enums;

public enum UserRole {
    STUDENT,
    INSTRUCTOR,
    ADMIN;

    public boolean isAdmin() { return this == ADMIN; }
    public boolean isInstructor() { return this == INSTRUCTOR; }
    public boolean isStudent() { return this == STUDENT; }
}