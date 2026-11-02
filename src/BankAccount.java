public final class BankAccount {
    private final String owner;
    private final int id;
    private Balance balance;
    private static int accountCount;
    private boolean isBlocked;

    public BankAccount(String owner, Balance balance) {
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("Имя владельца аккаунта не может быть пустым");
        }
        if (balance == null) {
            throw new IllegalArgumentException("Баланс не может быть null");
        }

        id = ++accountCount;

        this.owner = owner;
        this.balance = balance;
        isBlocked = false;
    }

    public String getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }

    public Balance getBalance() {
        return balance; // Balance иммутабельный
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void block() {
        isBlocked = true;
    }

    public void unblock() {
        isBlocked = false;
    }

    private void ensureActive() {
        if (isBlocked) {
            throw new IllegalStateException("Аккаунт №" + id + " заблокирован");
        }
    }

    public void deposit(long amount) {
        ensureActive();
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть больше 0: " + amount);
        }
        this.balance = this.balance.add(amount);
    }

    public void withdraw(long amount) {
        ensureActive();
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть больше 0: " + amount);
        }
        this.balance = this.balance.subtract(amount);
    }

    public void transfer(BankAccount to, long amount) {
        if (to == null) {
            throw new IllegalArgumentException("Целевой аккаунт не может быть null");
        }
        if (to == this) {
            throw new IllegalArgumentException("Нельзя переводить самому себе");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть больше 0: " + amount);
        }
        ensureActive();
        to.ensureActive();

        if (this.balance.getCurrency() != to.balance.getCurrency()) {
            throw new IllegalArgumentException("Валюты аккаунтов не совпадают: " + this.balance.getCurrency() + " и " + to.balance.getCurrency());
        }

        // Сначала считаются новые балансы, а затем только присваиваются.
        Balance newFrom = this.balance.subtract(amount);
        Balance newTo = to.balance.add(amount);

        this.balance = newFrom;
        to.balance = newTo;
    }

    @Override
    public String toString() {
        return "Банковский аккаунт №" + id + "{владелец='" + owner + "', баланс=" + balance + ", блокировка=" + isBlocked + "}";
    }
}
