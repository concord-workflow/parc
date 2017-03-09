package io.takari.parc;

import java.util.function.Function;

@FunctionalInterface
public interface Parser<I, O> {

    Result<I, O> apply(Input<I> in);

    default Result<I, O> parse(Input<I> in) {
        return apply(in);
    }

    default <X> Parser<I, X> bind(Function<O, Parser<I, X>> f) {
        return Combinators.bind(this, f);
    }

    default <X> Parser<I, X> then(Parser<I, X> p) {
        return Combinators.then(this, p);
    }

    default Parser<I, Seq<O>> many() {
        return Combinators.many(this);
    }

    default Parser<I, Seq<O>> many1() {
        return Combinators.many1(this);
    }

    default <X> Parser<I, X> map(Function<O, X> f) {
        return Combinators.map(this, f);
    }

    static <I, O> Ref<I, O> ref() {
        return new Ref<>();
    }

    class Ref<I, O> implements Parser<I, O> {

        private Parser<I, O> delegate;

        public void set(Parser<I, O> p) {
            this.delegate = p;
        }

        @Override
        public Result<I, O> apply(Input<I> in) {
            return delegate.apply(in);
        }
    }
}
