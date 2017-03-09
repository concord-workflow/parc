package io.takari.parc;

import io.takari.parc.Result.Failure;
import io.takari.parc.Result.Success;

import java.util.function.Function;
import java.util.function.Predicate;

public final class Combinators {

    public static <I, O> Parser<I, O> retn(O val) {
        return in -> ok(val, in);
    }

    public static <I, O> Result<I, O> ok(O result, Input<I> next) {
        return new Success<>(true, result, next);
    }

    public static <I, O> Result<I, O> fail(Input<I> input, String expected) {
        return new Failure<>(input.position(), expected);
    }

    public static <I> Parser<I, I> satisfy(Predicate<I> p) {
        return in -> {
            if (in.end()) {
                return fail(in, "EOF");
            }

            I val = in.first();
            if (p.test(val)) {
                return ok(val, in.rest());
            }

            return fail(in, null);
        };
    }

    public static <I> Parser<I, I> satisfy(I value) {
        return satisfy(value::equals);
    }

    public static <I, O, X> Parser<I, X> bind(Parser<I, ? extends O> p, Function<O, Parser<I, X>> f) {
        return in -> {
            Result<I, ? extends O> r = p.apply(in);
            if (r.isFailure()) {
                return r.cast();
            }

            Success<I, ? extends O> s = r.toSuccess();
            return f.apply(s.getResult()).apply(s.getRest());
        };
    }

    public static <I, O, X> Parser<I, X> then(Parser<I, O> p, Parser<I, X> q) {
        return in -> {
            Result<I, O> rp = p.apply(in);
            if (!rp.isSuccess()) {
                return rp.cast();
            }

            return q.apply(rp.getRest());
        };
    }

    public static <I, O> Parser<I, O> or(Parser<I, ? extends O> p, Parser<I, ? extends O> q) {
        return in -> {
            Result<I, ? extends O> rp = p.apply(in);
            if (rp.isSuccess()) {
                return rp.cast();
            }
            return q.apply(in).cast();
        };
    }

    public static <I, O, X> Parser<I, X> map(Parser<I, O> p, Function<O, X> f) {
        return p.bind(f.andThen(Combinators::retn));
    }

    public static <I, O> Parser<I, O> label(String name, Parser<I, O> p) {
        return in -> {
            Result<I, O> r = p.apply(in);
            if (r.isFailure()) {
                // TODO add to the expected list
                Failure<I, O> f = r.toFailure();
                String n = f.getMessage() != null ? f.getMessage() : name;
                return new Failure<>(f.getPosition(), n);
            }
            return r;
        };
    }

    public static <I, O> Parser<I, O> choice(
            Parser<I, ? extends O> p1,
            Parser<I, ? extends O> p2) {

        return or(p1, p2);
    }

    public static <I, O> Parser<I, O> choice(
            Parser<I, ? extends O> p1,
            Parser<I, ? extends O> p2,
            Parser<I, ? extends O> p3) {

        return or(p1, or(p2, p3));
    }

    public static <I, O> Parser<I, O> choice(
            Parser<I, ? extends O> p1,
            Parser<I, ? extends O> p2,
            Parser<I, ? extends O> p3,
            Parser<I, ? extends O> p4) {

        return or(p1, or(p2, or(p3, p4)));
    }

    public static <I, O> Parser<I, O> choice(
            Parser<I, ? extends O> p1,
            Parser<I, ? extends O> p2,
            Parser<I, ? extends O> p3,
            Parser<I, ? extends O> p4,
            Parser<I, ? extends O> p5) {

        return or(p1, or(p2, or(p3, or(p4, p5))));
    }

    public static <I, O> Parser<I, O> choice(
            Parser<I, ? extends O> p1,
            Parser<I, ? extends O> p2,
            Parser<I, ? extends O> p3,
            Parser<I, ? extends O> p4,
            Parser<I, ? extends O> p5,
            Parser<I, ? extends O> p6) {

        return or(p1, or(p2, or(p3, or(p4, or(p5, p6)))));
    }

    public static <I, O> Parser<I, Seq<O>> many(Parser<I, O> p) {
        return loop(p, Seq.empty(), -1);
    }

    public static <I, O> Parser<I, Seq<O>> many1(Parser<I, O> p) {
        return bind(p, x -> loop(p, Seq.of(x), -1));
    }
    
    private static <I, O> Parser<I, Seq<O>> loop(Parser<I, O> p, Seq<O> acc, int count) {
        return in -> {
            Input<I> in2 = in;
            Seq<O> acc2 = acc;

            int i = 0;
            for (; i != count; ++i) {
                if (in2.end()) {
                    break;
                }

                Result<I, O> r = p.apply(in2);
                if (r.isConsumed()) {
                    if (r.isSuccess()) {
                        in2 = r.getRest();
                        O val = r.toSuccess().getResult();
                        acc2 = acc2.add(val);
                    } else {
                        return r.cast();
                    }
                } else {
                    if (r.isSuccess()) {
                        in2 = r.getRest();
                        O val = r.toSuccess().getResult();
                        acc2 = acc2.add(val);
                    } else {
                        --i;
                        break;
                    }
                }
            }

            if (count < 0 || i == count) {
                return ok(acc2, in2);
            } else {
                return fail(in2, "Not enough elements");
            }
        };
    }

    public static <I, O, S, E> Parser<I, O> between(
            Parser<I, S> start,
            Parser<I, E> end,
            Parser<I, O> p) {
        return then(start, bind(p, x -> then(end, retn(x))));
    }

    public static <I, O> Parser<I, O> option(Parser<I, O> p, O x) {
        return or(p, retn(x));
    }

    private Combinators() {
    }
}
