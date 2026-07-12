package net.caramel.snare.event;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import org.slf4j.LoggerFactory;

public final class EventBus {
    private final Map<Class<?>, List<Registration<?>>> registrations = new LinkedHashMap<>();

    public <E> void subscribe(Class<E> type, EventPriority priority, BooleanSupplier active, Consumer<E> listener) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(priority);
        Objects.requireNonNull(active);
        Objects.requireNonNull(listener);
        registrations.computeIfAbsent(type, k -> new ArrayList<>()).add(new Registration<>(priority, active, listener));
    }

    @SuppressWarnings("unchecked")
    public <E> E post(E event) {
        Objects.requireNonNull(event);
        Class<?> eventClass = event.getClass();
        for (var entry : registrations.entrySet()) {
            if (!entry.getKey().isAssignableFrom(eventClass)) continue;
            List<Registration<?>> sorted = new ArrayList<>(entry.getValue());
            sorted.sort(Comparator.comparingInt(r -> r.priority.ordinal()));
            for (Registration<?> reg : sorted) {
                if (!reg.active.getAsBoolean()) continue;
                try {
                    ((Registration<E>) reg).listener.accept(event);
                } catch (RuntimeException e) {
                    LoggerFactory.getLogger("snare/events").error(
                        "Event listener failed for {}", entry.getKey().getSimpleName(), e
                    );
                }
            }
        }
        return event;
    }

    private record Registration<E>(EventPriority priority, BooleanSupplier active, Consumer<E> listener) {}
}
