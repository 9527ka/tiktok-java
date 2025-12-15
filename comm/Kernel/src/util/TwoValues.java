package util;

public class TwoValues<U, V> {
    private U one;
    private V two;

    public U getOne() {
        return one;
    }

    public void setOne(U one) {
        this.one = one;
    }

    public V getTwo() {
        return two;
    }

    public void setTwo(V two) {
        this.two = two;
    }

    public TwoValues(U one, V two) {
        this.one = one;
        this.two = two;
    }

    public TwoValues() {
    }
}