class Wallet {
    int balance;
    Wallet(int balance) {
        this.balance = balance;
    }
}
public class Main {
    // Deadlock-prone transfer
    static void transferDeadlock(
            Wallet from, Wallet to) {
        synchronized (from) {
            System.out.println(
                    Thread.currentThread().getName()
                    + " locked first wallet");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (to) {
                from.balance -= 100;
                to.balance += 100;
                System.out.println(
                        Thread.currentThread().getName()
                        + " transferred 100");
            }
        }
    }
    // Deadlock prevention using fixed lock ordering
    static void transferSafe(
            Wallet from, Wallet to) {
        Wallet first = from;
        Wallet second = to;
        // Always lock wallets in the same order
        if (System.identityHashCode(first)
                > System.identityHashCode(second)) {
            first = to;
            second = from;
        }
        synchronized (first) {
            synchronized (second) {
                from.balance -= 100;
                to.balance += 100;
                System.out.println(
                        Thread.currentThread().getName()
                        + " transferred 100 safely");
            }
        }
    }
    public static void main(String[] args)
            throws InterruptedException {
        Wallet walletA = new Wallet(1000);
        Wallet walletB = new Wallet(1000);
        System.out.println(
                "Initial Balance:");
        System.out.println(
                "Wallet A: " + walletA.balance);
        System.out.println(
                "Wallet B: " + walletB.balance);
        Thread t1 = new Thread(() ->
                transferSafe(walletA, walletB),
                "Student 1");
        Thread t2 = new Thread(() ->
                transferSafe(walletB, walletA),
                "Student 2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(
                "\nFinal Balance:");
        System.out.println(
                "Wallet A: " + walletA.balance);
        System.out.println(
                "Wallet B: " + walletB.balance);
        System.out.println(
                "\nTransfers completed without deadlock.");
    }
}
