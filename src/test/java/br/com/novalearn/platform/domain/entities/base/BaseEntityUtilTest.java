package br.com.novalearn.platform.domain.entities.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BaseEntityUtilTest {
    @Test
    void should_trim_string() {
        String result = BaseEntity.sanitize("  abc  ");
        assertEquals("abc", result);
    }

    @Test
    void should_return_null_when_value_is_null() {
        assertNull(BaseEntity.sanitize(null));
    }

    @Test
    void should_return_null_when_blank() {
        assertNull(BaseEntity.sanitize("   "));
    }
}