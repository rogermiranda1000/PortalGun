package com.rogermiranda1000.portalgun.blocks.decorators;

import com.rogermiranda1000.portalgun.PortalGun;

import java.lang.reflect.Constructor;

public class DecoratorFactory<T extends Decorator> {
    private final Constructor<T> constructor;

    public DecoratorFactory(Class<T> target) {
        Constructor<T> constructor = null;
        try {
            constructor = target.getConstructor();
        } catch (NoSuchMethodException ex) {
            PortalGun.plugin.reportException(ex);
        }
        this.constructor = constructor;
    }

    public Decorator getDecorator() {
        if (this.constructor != null) {
            try {
                return this.constructor.newInstance();
            } catch (Exception ex) {
                PortalGun.plugin.reportException(ex);
            }
        }

        return null; // failed
    }
}
