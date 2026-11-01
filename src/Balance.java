public final class Balance {
    private final long amountInSubunits;
    private final Currency currency;

    public Balance(long amountInSubunits, Currency currency) {
        if (amountInSubunits < 0) {
            throw new IllegalArgumentException("Сумма не может быть меньше нуля: " + amountInSubunits);
        }
        if (currency == null) {
            throw new IllegalArgumentException("Валюта не может быть null");
        }

        this.amountInSubunits = amountInSubunits;
        this.currency = currency;
    }

    public Balance(long amountInSubunits) {
        this(amountInSubunits, Currency.RUB);
    }

    public long getAmountInSubunits() {
        return amountInSubunits;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Balance add(long deltaAmountInSubunits) {
        if (deltaAmountInSubunits <= 0) {
            throw new IllegalArgumentException("Нельзя добавить сумму меньше или равную 0: " + deltaAmountInSubunits);
        }

        long newAmount;
        try {
            newAmount = Math.addExact(amountInSubunits, deltaAmountInSubunits);
        } catch (ArithmeticException e) {
            throw new ArithmeticException("Переполнение при сложении: " + amountInSubunits + " + " + deltaAmountInSubunits + " (" + e.getMessage() + ")");
        }

        return new Balance(newAmount, currency);
    }

    public Balance subtract(long deltaAmountInSubunits) {
        if (deltaAmountInSubunits <= 0) {
            throw new IllegalArgumentException("Нельзя отнять сумму меньше или равную 0: " + deltaAmountInSubunits);
        }
        if (deltaAmountInSubunits > amountInSubunits) {
            throw new IllegalArgumentException("Недостаточно средств: на счете " + amountInSubunits + ", пытались отнять " + deltaAmountInSubunits);
        }

        return new Balance(amountInSubunits - deltaAmountInSubunits, currency);
    }

    public Balance convert(Currency target) {
        if (target == null) {
            throw new IllegalArgumentException("Целевая валюта конвертации не может быть null");
        }

        if (target == this.currency) {
            return this;
        }

        long priceFrom = this.currency.getPriceInRublesKopecks();
        long priceTo = target.getPriceInRublesKopecks();

        long numerator;
        try {
            numerator = Math.multiplyExact(amountInSubunits, priceFrom);
        } catch (ArithmeticException e) {
            throw new ArithmeticException("Переполнение при конвертации: " + amountInSubunits + " * " + priceFrom);
        }

        long newAmount = (numerator + priceTo / 2) / priceTo;
        return new Balance(newAmount, target);
    }

    @Override
    public String toString() {
        long whole = amountInSubunits / 100;
        long frac  = amountInSubunits % 100;
        return String.format("Баланс: %d.%02d %s", whole, frac, currency.getSymbol());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Balance other = (Balance) object;
        return amountInSubunits == other.amountInSubunits && currency == other.currency;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(amountInSubunits, currency);
    }
}
