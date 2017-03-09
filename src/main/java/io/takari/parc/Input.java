package io.takari.parc;

public interface Input<T> {

    int position();

    T first();

    Input<T> rest();

    boolean end();

    static <T> Input<T> empty() {
        return new EmptyInput<>();
    }

    @SafeVarargs
    static <T> Input<T> of(T... items) {
        return new ArrayInput<>(items);
    }

    class EmptyInput<T> implements Input<T> {

        @Override
        public int position() {
            return 0;
        }

        @Override
        public T first() {
            throw new IllegalStateException("Empty");
        }

        @Override
        public Input<T> rest() {
            throw new IllegalStateException("Empty");
        }

        @Override
        public boolean end() {
            return true;
        }
    }

    class ArrayInput<T> implements Input<T> {

        private final T[] items;
        private final int pos;

        public ArrayInput(T[] items) {
            this(items, 0);
        }

        public ArrayInput(T[] items, int pos) {
            this.items = items;
            this.pos = pos;
        }

        @Override
        public int position() {
            return pos;
        }

        @Override
        public T first() {
            return items[pos];
        }

        @Override
        public Input<T> rest() {
            return new ArrayInput<>(items, pos + 1);
        }

        @Override
        public boolean end() {
            return pos >= items.length;
        }
    }
}
