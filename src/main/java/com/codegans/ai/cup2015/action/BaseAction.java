package com.codegans.ai.cup2015.action;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:28
 */
public abstract class BaseAction<A extends BaseAction<A>> implements Action<A> {
    private final int score;

    public BaseAction(int score) {
        this.score = score;
    }

    @Override
    public int score() {
        return score;
    }

    @Override
    public int compareTo(A o) {
        return Integer.compare(score(), o.score());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() ^ score;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass() == obj.getClass() && score() == ((BaseAction) obj).score();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + score + ']';
    }
}
