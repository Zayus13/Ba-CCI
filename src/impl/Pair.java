package impl;

import java.util.Objects;

public class Pair<T extends Comparable<T>> {
    T u, v;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<T> pair = (Pair<T>) o;
        return (u.equals(pair.u) && v.equals(pair.v)) || (v.equals(pair.u) && u.equals(pair.v));
    }

    public Pair(T u, T v) {
        this.u = u;
        this.v = v;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "u=" + u +
                ", v=" + v +
                '}';
    }

    @Override
    public int hashCode() {
        if (this.u.compareTo(this.v) <= 0) {
            return Objects.hash(u, v);
        } else {
            return Objects.hash(v, u);
        }

    }
}
