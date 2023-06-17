package com.leetcodeapi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestContainer extends AbstractTestContainer    {
    @Test
    void voidCanStartPostgresql() {
        assertThat(mySQLContainer.isRunning()).isTrue();
        assertThat(mySQLContainer.isCreated()).isTrue();
    }

}
