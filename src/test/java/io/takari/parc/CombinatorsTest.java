package io.takari.parc;

import org.junit.Test;

import static io.takari.parc.Combinators.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CombinatorsTest {

    @Test
    public void testRetn() {
        Parser<Integer, Integer> p = retn(123);
        assertSuccess(123, p.apply(Input.empty()));
    }

    @Test
    public void testSatisfy() {
        Parser<Integer, Integer> p = satisfy(i -> i == 123);
        assertSuccess(123, p.apply(Input.of(123)));
    }

    @Test
    public void testBind() {
        Parser<Integer, Integer> p = bind(retn(123), i -> retn(i + 1));
        assertSuccess(124, p.apply(Input.empty()));
    }

    @Test
    public void testThen() {
        Parser<Integer, Boolean> p = Combinators.<Integer, Integer>retn(123).then(retn(false));
        assertSuccess(false, p.apply(Input.empty()));
    }

    @Test
    public void testOr() {
        Parser<Integer, Integer> p = or(satisfy(234), satisfy(123));
        assertSuccess(123, p.apply(Input.of(123)));
    }

    @Test
    public void testMany() {
        Parser<Integer, Seq<Integer>> p1 = many(satisfy((Integer i) -> i > 0));
        assertSuccess(p1.apply(Input.of(1, 2, 3)));
        assertSuccess(p1.apply(Input.empty()));

        assertSuccess(p1.apply(Input.of(1, -1, 4)));

    }

    @Test
    public void testManyEarlyExit() {
        Parser<Integer, Integer> p2 = satisfy(1).then(many(satisfy(i -> i < 5))).then(satisfy(5));
        assertSuccess(p2.apply(Input.of(1, 2, 3, 4, 5)));
    }

    @Test
    public void testMany1() {
        Parser<Integer, Seq<Integer>> p = many1(satisfy((Integer i) -> i > 0));
        assertSuccess(p.apply(Input.of(1, 2, 3)));
        assertFailure(p.apply(Input.empty()));

        assertSuccess(Seq.of(1), p.apply(Input.of(1, -1, 4)));
    }

    @Test
    public void testBetween() {
        Parser<Integer, Integer> p = between(satisfy(1), satisfy(3), satisfy(2));
        assertSuccess(p.apply(Input.of(1, 2, 3)));
        assertFailure(p.apply(Input.of(1, 2, 4)));
    }

    private static <I, O> void assertSuccess(O expected, Result<I, O> result) {
        assertEquals(expected, result.toSuccess().getResult());
    }

    private static <I, O> void assertSuccess(Result<I, O> result) {
        assertTrue(result.isSuccess());
    }

    private static <I, O> void assertFailure(Result<I, O> result) {
        assertTrue(result.isFailure());
    }
}
