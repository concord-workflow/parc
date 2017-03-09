package io.takari.parc;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public interface Seq<T> {

    Seq<T> add(T item);

    Stream<T> stream();

    List<T> toList();

    static <T> Seq<T> empty() {
        return new Impl<>(Collections.emptyList());
    }

    @SafeVarargs
    static <T> Seq<T> of(T... items) {
        return new Impl<>(Arrays.asList(items));
    }

    class Impl<T> implements Seq<T> {

        private final List<T> delegate;

        private Impl(List<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Seq<T> add(T item) {
            List<T> l = new LinkedList<>(delegate);
            l.add(item);
            return new Impl<>(l);
        }

        @Override
        public Stream<T> stream() {
            return delegate.stream();
        }

        @Override
        public List<T> toList() {
            return delegate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Impl<?> impl = (Impl<?>) o;

            return delegate.equals(impl.delegate);
        }

        @Override
        public String toString() {
            return delegate.toString();
        }
    }
}
