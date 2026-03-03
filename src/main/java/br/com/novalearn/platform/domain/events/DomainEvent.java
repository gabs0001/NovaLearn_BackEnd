package br.com.novalearn.platform.domain.events;

import java.time.LocalDateTime;

public interface DomainEvent {  LocalDateTime occurredAt(); }