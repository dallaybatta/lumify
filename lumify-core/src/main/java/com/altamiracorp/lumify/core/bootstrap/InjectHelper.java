package com.altamiracorp.lumify.core.bootstrap;

import com.fasterxml.jackson.module.guice.ObjectMapperModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class InjectHelper {
    private static Injector injector;

    public static void inject(Object o, ModuleMaker moduleMaker) {
        if (injector == null) {
            injector = Guice.createInjector(moduleMaker.createModule(), new ObjectMapperModule());
        }
        inject(o);
    }

    public static void inject(Object o) {
        if (injector == null) {
            throw new RuntimeException("Could not find injector");
        }
        injector.injectMembers(o);
    }

    public static Injector getInjector() {
        return injector;
    }

    public static <T> T getInstance(Class<T> clazz, ModuleMaker moduleMaker) {
        if (injector == null) {
            injector = Guice.createInjector(moduleMaker.createModule());
        }
        return injector.getInstance(clazz);
    }

    public static interface ModuleMaker {
        Module createModule();
    }
}
