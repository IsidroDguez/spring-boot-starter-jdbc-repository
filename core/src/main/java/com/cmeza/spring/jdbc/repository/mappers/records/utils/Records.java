package com.cmeza.spring.jdbc.repository.mappers.records.utils;

import java.lang.invoke.*;
import java.lang.reflect.RecordComponent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Records {

    private static final ConcurrentHashMap<Class<?>, Plan<?>> CACHE = new ConcurrentHashMap<>();

    public static <R extends Record> R with(R entity, Map<String, ?> overridesByName) {
        if (overridesByName == null || overridesByName.isEmpty()) return entity;

        @SuppressWarnings("unchecked")
        Plan<R> plan = (Plan<R>) CACHE.computeIfAbsent(entity.getClass(), Records::buildPlan);

        Object[] overrides = new Object[plan.componentCount];
        long mask = 0;

        for (var e : overridesByName.entrySet()) {
            Integer idx = plan.indexByName.get(e.getKey());
            if (idx == null) {
                throw new IllegalArgumentException(
                        "'" + e.getKey() + "' not exists in record " + entity.getClass().getName()
                );
            }
            int i = idx;
            overrides[i] = e.getValue();
            mask |= (1L << i);
        }

        return plan.copier.with(entity, overrides, mask);
    }

    private static <R extends Record> Plan<R> buildPlan(Class<?> rawType) {
        if (!rawType.isRecord()) {
            throw new IllegalArgumentException("Solo records. Recibido: " + rawType);
        }

        @SuppressWarnings("unchecked")
        Class<R> recordType = (Class<R>) rawType;

        RecordComponent[] components = recordType.getRecordComponents();
        int n = components.length;

        if (n > 63) {
            throw new IllegalArgumentException("A maximum of 63 attributes are allowed. Record: " + recordType);
        }

        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(recordType, lookup);

            var indexByName = new java.util.HashMap<String, Integer>(n * 2);
            Class<?>[] ctorTypes = new Class<?>[n];
            MethodHandle[] accessors = new MethodHandle[n];

            for (int i = 0; i < n; i++) {
                var c = components[i];
                indexByName.put(c.getName(), i);

                ctorTypes[i] = c.getType();

                MethodHandle acc = privateLookup.findVirtual(
                        recordType,
                        c.getName(),
                        MethodType.methodType(c.getType())
                );

                accessors[i] = acc.asType(MethodType.methodType(Object.class, recordType));
            }

            MethodHandle ctor = privateLookup.findConstructor(
                    recordType,
                    MethodType.methodType(void.class, ctorTypes)
            );

            MethodHandle ctorSpreader = ctor.asSpreader(Object[].class, n);

            Plan<R> plan = new Plan<>(recordType, Map.copyOf(indexByName), n, accessors, ctorSpreader, null);
            plan.copier = createCopier(plan);

            return plan;

        } catch (Throwable t) {
            throw new IllegalStateException("No se pudo construir plan para " + recordType.getName(), t);
        }
    }

    private static <R extends Record> RecordCopier<R> createCopier(Plan<R> plan) throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        MethodHandle impl = lookup.findStatic(
                Records.class,
                "withImpl",
                MethodType.methodType(Record.class, Plan.class, Record.class, Object[].class, long.class)
        );

        MethodType sam = MethodType.methodType(Record.class, Record.class, Object[].class, long.class);

        MethodType invokedType = MethodType.methodType(RecordCopier.class, Plan.class);

        CallSite site = LambdaMetafactory.metafactory(
                lookup,
                "with",
                invokedType,
                sam,
                impl,
                sam
        );

        @SuppressWarnings("unchecked")
        RecordCopier<R> copier = (RecordCopier<R>) site.getTarget().invoke(plan);
        return copier;
    }

    private static Record withImpl(Plan<?> plan, Record original, Object[] overrides, long mask) {
        Object[] args = new Object[plan.componentCount];

        for (int i = 0; i < plan.componentCount; i++) {
            long bit = 1L << i;

            if ((mask & bit) != 0L) {
                args[i] = overrides[i];
            } else {
                try {
                    args[i] = plan.accessors[i].invoke(original);
                } catch (Throwable t) {
                    throw sneaky(t);
                }
            }
        }

        try {
            return (Record) plan.ctorSpreader.invoke(args);
        } catch (Throwable t) {
            throw sneaky(t);
        }
    }

    private static RuntimeException sneaky(Throwable t) {
        if (t instanceof RuntimeException re) return re;
        if (t instanceof Error e) throw e;
        return new IllegalStateException(t);
    }

    @FunctionalInterface
    public interface RecordCopier<R extends Record> {
        R with(R original, Object[] overrides, long mask);
    }

    private static final class Plan<R extends Record> {
        final Class<R> recordType;
        final Map<String, Integer> indexByName;
        final int componentCount;
        final MethodHandle[] accessors;
        final MethodHandle ctorSpreader;
        RecordCopier<R> copier;

        Plan(Class<R> recordType,
             Map<String, Integer> indexByName,
             int componentCount,
             MethodHandle[] accessors,
             MethodHandle ctorSpreader,
             RecordCopier<R> copier) {
            this.recordType = recordType;
            this.indexByName = indexByName;
            this.componentCount = componentCount;
            this.accessors = accessors;
            this.ctorSpreader = ctorSpreader;
            this.copier = copier;
        }
    }
}
