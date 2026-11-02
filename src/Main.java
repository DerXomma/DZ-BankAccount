public class Main {
    public static void main(String[] args) {
        BankAccount acc1 = new BankAccount("Иван", new Balance(100_00));
        BankAccount acc2 = new BankAccount("Алиса",   new Balance(50_00));

        acc1.transfer(acc2, 30_00);
        System.out.println(acc1);
        System.out.println(acc2);

        try {
            acc1.transfer(acc2, 1_000_00);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        try {
            acc1.transfer(acc1, 1_00);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        acc1.block();
        try {
            acc1.deposit(10_00);
        } catch (IllegalStateException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        System.out.println(acc1);
        System.out.println(acc2);
    }
}
