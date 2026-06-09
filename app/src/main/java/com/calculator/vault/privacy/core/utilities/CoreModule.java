package com.calculator.vault.privacy.core.utilities;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public final class CoreModule {
    @Provides
    @Singleton
    static CalculatorEngine provideCalculatorEngine() {
        return new CalculatorEngine();
    }
}
