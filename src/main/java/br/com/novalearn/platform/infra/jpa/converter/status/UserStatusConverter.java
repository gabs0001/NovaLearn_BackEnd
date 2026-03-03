package br.com.novalearn.platform.infra.jpa.converter.status;

import br.com.novalearn.platform.domain.enums.UserStatus;
import br.com.novalearn.platform.infra.jpa.converter.AbstractEnumConverter;

public class UserStatusConverter extends AbstractEnumConverter<UserStatus> {
    public UserStatusConverter() {
        super(UserStatus.class);
    }
}