package io.takari.parc;

public interface Result<I, O> {

    Input<I> getRest();

    default boolean isConsumed() {
        return false;
    }

    @SuppressWarnings("unchecked")
    default <A, B> Result<A, B> cast() {
        return (Result<A, B>) this;
    }

    default boolean isSuccess() {
        return this instanceof Success;
    }

    default boolean isFailure() {
        return this instanceof Failure;
    }

    default Success<I, O> toSuccess() {
        if (!isSuccess()) {
            throw new IllegalStateException("Expected a 'Success' state");
        }
        return (Success<I, O>) this;
    }

    default Failure<I, O> toFailure() {
        if (!isFailure()) {
            throw new IllegalStateException("Expected a 'Failure' state");
        }
        return (Failure<I, O>) this;
    }

    class Success<I, O> implements Result<I, O> {

        private final boolean consumed;
        private final O result;
        private final Input<I> rest;

        public Success(boolean consumed, O result, Input<I> rest) {
            this.consumed = consumed;
            this.result = result;
            this.rest = rest;
        }

        @Override
        public boolean isConsumed() {
            return consumed;
        }

        public O getResult() {
            return result;
        }

        @Override
        public Input<I> getRest() {
            return rest;
        }

        @Override
        public String toString() {
            return "Success{" +
                    "result=" + result +
                    ", rest=" + rest +
                    '}';
        }
    }

    class Failure<I, O> implements Result<I, O> {

        private final int position;
        private final String message;

        public Failure(int position, String message) {
            this.position = position;
            this.message = message;
        }

        public int getPosition() {
            return position;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public Input<I> getRest() {
            throw new IllegalStateException("Not allowed");
        }

        @Override
        public String toString() {
            return "Failure{" +
                    "position=" + position +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
