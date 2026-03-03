package br.com.novalearn.platform.factories;

import br.com.novalearn.platform.api.dtos.password.ChangePasswordRequestDTO;

public final class CommonTestFactory {
    private CommonTestFactory() {}

    public static ChangePasswordRequestDTO changePasswordRequest() {
        return new ChangePasswordRequestDTO(
                "old123",
                "new123456",
                "new123456"
        );
    }
}